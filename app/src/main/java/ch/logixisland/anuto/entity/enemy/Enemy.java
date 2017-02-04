package ch.logixisland.anuto.entity.enemy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.shape.HealthBar;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.data.EnemyConfig;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.math.vector.Vector2;


public abstract class Enemy extends Entity {

    public static Predicate<Enemy> enabled() {
        return new Predicate<Enemy>() {
            @Override
            public boolean apply(Enemy enemy) {
                return enemy.isEnabled();
            }
        };
    }

    public static Function<Enemy, Float> health() {
        return new Function<Enemy, Float>() {
            @Override
            public Float apply(Enemy input) {
                return input.mHealth;
            }
        };
    }

    public static Function<Enemy, Float> distanceRemaining() {
        return new Function<Enemy, Float>() {
            @Override
            public Float apply(Enemy input) {
                return input.getDistanceRemaining();
            }
        };
    }

    private final EnemyConfig mConfig;

    private boolean mEnabled = true;
    private float mHealth;
    private float mBaseSpeed;
    private float mHealthModifier = 1f;
    private float mRewardModifier = 1f;
    private float mSpeedModifier = 1f;
    private List<Vector2> mWayPoints = null;
    private int mWayPointIndex;
    private HealthBar mHealthBar;

    private final List<EnemyListener> mListeners = new CopyOnWriteArrayList<>();

    public Enemy(EnemyConfig config) {
        mConfig = config;
        mBaseSpeed = mConfig.getSpeed();
        mHealth = mConfig.getHealth();

        mHealthBar = getShapeFactory().createHealthBar(this);
    }

    @Override
    public final int getType() {
        return Types.ENEMY;
    }

    @Override
    public void init() {
        super.init();
        getGameEngine().add(mHealthBar);
    }

    @Override
    public void clean() {
        super.clean();
        getGameEngine().remove(mHealthBar);

        for (EnemyListener listener : mListeners) {
            listener.enemyRemoved(this);
        }

        mListeners.clear();
    }

    @Override
    public void tick() {
        super.tick();

        if (!mEnabled) {
            return;
        }

        if (!hasWayPoint()) {
            for (EnemyListener listener : mListeners) {
                listener.enemyFinished(this);
            }
            remove();
            return;
        }

        float stepSize = getSpeed() / GameEngine.TARGET_FRAME_RATE;
        if (getDistanceTo(getWayPoint()) >= stepSize) {
            move(getDirection().mul(stepSize));
        } else {
            setPosition(getWayPoint());
            mWayPointIndex++;
        }
    }


    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public void setPathIndex(int pathIndex) {
        mWayPoints = getLevelDescriptor().getPaths().get(pathIndex).getWayPoints();
        setPosition(mWayPoints.get(0));
        mWayPointIndex = 1;
    }

    private Vector2 getWayPoint() {
        return mWayPoints.get(mWayPointIndex);
    }

    boolean hasWayPoint() {
        return mWayPoints != null && mWayPointIndex < mWayPoints.size();
    }

    private float getSpeed() {
        float speed = mBaseSpeed * mSpeedModifier;
        float minSpeed = getGameSettings().getMinSpeedModifier() * getConfigSpeed();
        return Math.max(minSpeed, speed);
    }

    float getConfigSpeed() {
        return mConfig.getSpeed();
    }

    void setBaseSpeed(float baseSpeed) {
        mBaseSpeed = baseSpeed;
    }

    Vector2 getDirection() {
        if (!hasWayPoint()) {
            return null;
        }

        return getDirectionTo(getWayPoint());
    }

    private float getDistanceRemaining() {
        if (!hasWayPoint()) {
            return 0;
        }

        float dist = getDistanceTo(getWayPoint());

        for (int i = mWayPointIndex + 1; i < mWayPoints.size(); i++) {
            Vector2 wThis = mWayPoints.get(i);
            Vector2 wLast = mWayPoints.get(i - 1);

            dist += wThis.copy().sub(wLast).len();
        }

        return dist;
    }

    public Vector2 getPositionAfter(float sec) {
        if (mWayPoints == null) {
            return getPosition();
        }

        float distance = sec * getSpeed();
        int index = mWayPointIndex;
        Vector2 position = getPosition().copy();

        while (index < mWayPoints.size()) {
            Vector2 toWaypoint = mWayPoints.get(index).copy().sub(position);
            float toWaypointDist = toWaypoint.len();

            if (distance < toWaypointDist) {
                return position.add(toWaypoint.mul(distance / toWaypointDist));
            } else {
                distance -= toWaypointDist;
                position.set(mWayPoints.get(index));
                index++;
            }
        }

        return position;
    }

    public void sendBack(float dist) {
        int index = mWayPointIndex - 1;
        Vector2 pos = getPosition().copy();

        while (index > 0) {
            Vector2 wp = mWayPoints.get(index);
            Vector2 toWp = Vector2.fromTo(pos, wp);
            float toWpLen = toWp.len();

            if (dist > toWpLen) {
                dist -= toWpLen;
                pos = wp;
                index--;
            } else {
                pos = toWp.norm().mul(dist).add(pos);
                setPosition(pos);
                mWayPointIndex = index + 1;
                return;
            }
        }

        setPosition(mWayPoints.get(0));
        mWayPointIndex = 1;
    }


    public float getHealth() {
        return mHealth * mHealthModifier;
    }

    public float getHealthMax() {
        return mConfig.getHealth() * mHealthModifier;
    }

    public void damage(float dmg, Entity origin) {
        if (origin != null && origin instanceof Tower) {
            Tower originTower = (Tower) origin;

            if (mConfig.getStrongAgainst().contains(originTower.getWeaponType())) {
                dmg *= getGameSettings().getStrongAgainstModifier();
            }

            if (mConfig.getWeakAgainst().contains(originTower.getWeaponType())) {
                dmg *= getGameSettings().getWeakAgainstModifier();
            }

            originTower.reportDamageInflicted(dmg);
        }

        mHealth -= dmg / mHealthModifier;

        if (mHealth <= 0) {
            for (EnemyListener listener : mListeners) {
                listener.enemyKilled(this);
            }

            remove();
        }
    }

    public void heal(float val) {
        mHealth += val / mHealthModifier;

        if (mHealth > mConfig.getHealth()) {
            mHealth = mConfig.getHealth();
        }
    }

    public int getReward() {
        return Math.round(mConfig.getReward() * mRewardModifier);
    }


    public void modifySpeed(float f) {
        mSpeedModifier *= f;
    }

    public void modifyHealth(float f) {
        mHealthModifier *= f;
    }

    public void modifyReward(float f) {
        mRewardModifier *= f;
    }


    public void addListener(EnemyListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(EnemyListener listener) {
        mListeners.remove(listener);
    }


    float getProperty(String name) {
        return mConfig.getProperties().get(name);
    }
}
