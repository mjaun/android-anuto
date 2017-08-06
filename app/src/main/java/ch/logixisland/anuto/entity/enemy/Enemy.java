package ch.logixisland.anuto.entity.enemy;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.data.setting.EnemyProperties;
import ch.logixisland.anuto.data.setting.WeaponType;
import ch.logixisland.anuto.engine.logic.Entity;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.math.Vector2;


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

    private final EnemyProperties mConfig;

    private boolean mEnabled = true;
    private float mHealth;
    private float mMaxHealth;
    private int mReward;
    private float mBaseSpeed;
    private float mSpeedModifier = 1f;
    private List<Vector2> mWayPoints = null;
    private int mWayPointIndex;
    private Collection<WeaponType> mStrongAgainst;
    private Collection<WeaponType> mWeakAgainst;

    private float mMinSpeedModifier;
    private float mStrongAgainstModifier;
    private float mWeakAgainstModifier;

    private HealthBar mHealthBar;

    private final List<EnemyListener> mListeners = new CopyOnWriteArrayList<>();

    Enemy(GameEngine gameEngine, EnemyProperties config) {
        super(gameEngine);

        mConfig = config;
        mHealthBar = new HealthBar(getTheme(), this);
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

    public void setupPath(List<Vector2> wayPoints, float offset) {
        mWayPoints = wayPoints;
        Vector2 startPosition = mWayPoints.get(0);
        Vector2 startDirection = startPosition.to(mWayPoints.get(1)).norm();
        setPosition(startPosition.add(startDirection.mul(-offset)));
        mWayPointIndex = 1;
    }

    private Vector2 getWayPoint() {
        return mWayPoints.get(mWayPointIndex);
    }

    boolean hasWayPoint() {
        return mWayPoints != null && mWayPointIndex < mWayPoints.size();
    }

    public float getSpeed() {
        return mBaseSpeed * mSpeedModifier;
    }

    void setBaseSpeed(float baseSpeed) {
        mBaseSpeed = baseSpeed;
    }

    void setMinSpeedModifier(float minSpeedModifier) {
        mMinSpeedModifier = minSpeedModifier;
    }

    public void modifySpeed(float f) {
        mSpeedModifier = Math.max(mMinSpeedModifier, mSpeedModifier * f);
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

            dist += wLast.to(wThis).len();
        }

        return dist;
    }

    public Vector2 getPositionAfter(float sec) {
        if (mWayPoints == null) {
            return getPosition();
        }

        float distance = sec * getSpeed();
        int index = mWayPointIndex;
        Vector2 position = getPosition();

        while (index < mWayPoints.size()) {
            Vector2 toWaypoint = position.to(mWayPoints.get(index));
            float toWaypointDist = toWaypoint.len();

            if (distance < toWaypointDist) {
                return position.add(toWaypoint.mul(distance / toWaypointDist));
            } else {
                distance -= toWaypointDist;
                mWayPoints.get(index);
                index++;
            }
        }

        return position;
    }

    public void sendBack(float dist) {
        int index = mWayPointIndex - 1;
        Vector2 pos = getPosition();

        while (index > 0) {
            Vector2 wp = mWayPoints.get(index);
            Vector2 toWp = pos.to(wp);
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
        return mHealth;
    }

    public float getMaxHealth() {
        return mMaxHealth;
    }

    void resetHealth(float health) {
        mHealth = health;
        mMaxHealth = health;
    }

    public void modifyHealth(float f) {
        mHealth *= f;
        mMaxHealth *= f;
    }

    public void damage(float dmg, Entity origin) {
        if (origin != null && origin instanceof Tower) {
            Tower originTower = (Tower) origin;

            if (mStrongAgainst.contains(originTower.getWeaponType())) {
                dmg *= mStrongAgainstModifier;
            }

            if (mWeakAgainst.contains(originTower.getWeaponType())) {
                dmg *= mWeakAgainstModifier;
            }

            originTower.reportDamageInflicted(dmg);
        }

        mHealth -= dmg;

        if (mHealth <= 0) {
            for (EnemyListener listener : mListeners) {
                listener.enemyKilled(this);
            }

            remove();
        }
    }

    public void heal(float val) {
        mHealth += val;

        if (mHealth > mMaxHealth) {
            mHealth = mMaxHealth;
        }
    }

    void setStrongAgainst(Collection<WeaponType> strongAgainst) {
        mStrongAgainst = strongAgainst;
    }

    void setWeakAgainst(Collection<WeaponType> weakAgainst) {
        mWeakAgainst = weakAgainst;
    }

    void setStrongAgainstModifier(float strongAgainstModifier) {
        mStrongAgainstModifier = strongAgainstModifier;
    }

    void setWeakAgainstModifier(float weakAgainstModifier) {
        mWeakAgainstModifier = weakAgainstModifier;
    }

    public int getReward() {
        return mReward;
    }

    public void setReward(int reward) {
        mReward = reward;
    }

    public void modifyReward(float f) {
        mReward = Math.round(mReward * f);
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
