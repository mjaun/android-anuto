package ch.logixisland.anuto.game.entity.enemy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.data.EnemyConfig;
import ch.logixisland.anuto.game.data.Path;
import ch.logixisland.anuto.game.render.shape.HealthBar;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.math.vector.Vector2;


public abstract class Enemy extends Entity {

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

    private EnemyConfig mConfig;
    private float mHealth;
    private float mBaseSpeed;
    private float mHealthModifier = 1f;
    private float mRewardModifier = 1f;
    private float mSpeedModifier = 1f;
    private Path mPath = null;
    private int mWayPointIndex;
    private HealthBar mHealthBar;

    private final List<EnemyListener> mListeners = new CopyOnWriteArrayList<>();

    public Enemy() {
        mConfig = getLevel().getEnemyConfig(this);
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

        if (!isEnabled()) {
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
            move(getDirection(), stepSize);
        } else {
            setPosition(getWayPoint());
            mWayPointIndex++;
        }
    }

    public void setPathIndex(int pathIndex) {
        mPath = getLevel().getPaths().get(pathIndex);
        setPosition(mPath.get(0));
        mWayPointIndex = 1;
    }

    public float getSpeed() {
        float speed = mBaseSpeed * mSpeedModifier;
        float minSpeed = getLevel().getSettings().getMinSpeedModifier() * getConfigSpeed();
        return Math.max(minSpeed, speed);
    }

    protected float getConfigSpeed() {
        return mConfig.getSpeed();
    }

    protected void setBaseSpeed(float baseSpeed) {
        mBaseSpeed = baseSpeed;
    }

    public Vector2 getDirection() {
        if (!hasWayPoint()) {
            return null;
        }

        return getDirectionTo(getWayPoint());
    }

    public Vector2 getPositionAfter(float sec) {
        if (mPath == null) {
            return getPosition();
        }

        float distance = sec * getSpeed();
        int index = mWayPointIndex;
        Vector2 position = getPosition().copy();

        while (index < mPath.size()) {
            Vector2 toWaypoint = mPath.get(index).copy().sub(position);
            float toWaypointDist = toWaypoint.len();

            if (distance < toWaypointDist) {
                return position.add(toWaypoint.mul(distance / toWaypointDist));
            } else {
                distance -= toWaypointDist;
                position.set(mPath.get(index));
                index++;
            }
        }

        return position;
    }

    public float getDistanceRemaining() {
        if (!hasWayPoint()) {
            return 0;
        }

        float dist = getDistanceTo(getWayPoint());

        for (int i = mWayPointIndex + 1; i < mPath.size(); i++) {
            Vector2 wThis = mPath.get(i);
            Vector2 wLast = mPath.get(i - 1);

            dist += wThis.copy().sub(wLast).len();
        }

        return dist;
    }

    public void sendBack(float dist) {
        int index = mWayPointIndex - 1;
        Vector2 pos = getPosition().copy();

        while (index > 0) {
            Vector2 wp = mPath.get(index);
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

        setPosition(mPath.get(0));
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
            Tower originTower = (Tower)origin;

            if (originTower.getConfig().getStrongAgainstEnemies().contains(getClass())) {
                dmg *= getLevel().getSettings().getStrongAgainstModifier();
            }

            if (originTower.getConfig().getWeakAgainstEnemies().contains(getClass())) {
                dmg *= getLevel().getSettings().getWeakAgainstModifier();
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


    protected Vector2 getWayPoint() {
        return mPath.get(mWayPointIndex);
    }

    protected boolean hasWayPoint() {
        return mPath != null && mWayPointIndex < mPath.size();
    }

    public float getProperty(String name) {
        return mConfig.getProperties().get(name);
    }
}
