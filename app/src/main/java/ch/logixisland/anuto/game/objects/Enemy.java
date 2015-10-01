package ch.logixisland.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TypeIds;
import ch.logixisland.anuto.game.data.EnemyConfig;
import ch.logixisland.anuto.game.data.Path;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.Vector2;


public abstract class Enemy extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.ENEMY;

    private static final float HEALTHBAR_WIDTH = 1.0f;
    private static final float HEALTHBAR_HEIGHT = 0.1f;
    private static final float HEALTHBAR_OFFSET = 0.6f;

    /*
    ------ Healthbar Class ------
     */

    private class HealthBar extends DrawObject {
        private Paint mHealthBarBg;
        private Paint mHealthBarFg;

        public HealthBar() {
            mHealthBarBg = new Paint();
            mHealthBarBg.setColor(Color.BLACK);
            mHealthBarFg = new Paint();
            mHealthBarFg.setColor(Color.GREEN);
        }

        @Override
        public int getLayer() {
            return Layers.ENEMY_HEALTHBAR;
        }

        @Override
        public void draw(Canvas canvas) {
            if (!MathUtils.equals(mHealth, mConfig.health, 1f)) {
                canvas.save();
                canvas.translate(getPosition().x - HEALTHBAR_WIDTH / 2f, getPosition().y + HEALTHBAR_OFFSET);

                canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
                canvas.drawRect(0, 0, mHealth / mConfig.health * HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarFg);
                canvas.restore();
            }
        }
    }

    /*
    ------ Static ------
     */

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

    /*
    ------ Members -----
     */

    private EnemyConfig mConfig;

    private float mHealth;
    private float mBaseSpeed;

    private float mHealthModifier = 1f;
    private float mRewardModifier = 1f;
    private float mSpeedModifier = 1f;

    private Path mPath = null;
    private int mWayPointIndex;

    private HealthBar mHealthBar;

    /*
    ------ Constructors ------
     */

    public Enemy() {
        mConfig = getManager().getLevel().getEnemyConfig(this);
        mBaseSpeed = mConfig.speed;
        mHealth = mConfig.health;

        mHealthBar = new HealthBar();
    }

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mHealthBar);
    }

    @Override
    public void clean() {
        super.clean();
        getGame().remove(mHealthBar);
    }

    @Override
    public void tick() {
        super.tick();

        if (isEnabled()) {
            if (!hasWayPoint()) {
                getManager().takeLives(1);
                this.remove();
                return;
            }

            float stepSize = getSpeed() / GameEngine.TARGET_FRAME_RATE;
            if (getDistanceTo(getWayPoint()) < stepSize) {
                setPosition(getWayPoint());
                mWayPointIndex++;
            } else {
                move(getDirection(), stepSize);
            }
        }
    }


    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;

        setPosition(mPath.get(0));
        mWayPointIndex = 1;
    }


    public float getSpeed() {
        float speed = mBaseSpeed * mSpeedModifier;
        float minSpeed = getManager().getSettings().minSpeedModifier * getConfigSpeed();

        return Math.max(minSpeed, speed);
    }

    protected float getConfigSpeed() {
        return mConfig.speed;
    }

    protected float getBaseSpeed() {
        return mBaseSpeed;
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

        while (index < mPath.count()) {
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

        for (int i = mWayPointIndex + 1; i < mPath.count(); i++) {
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
        return mConfig.health * mHealthModifier;
    }

    public void damage(float dmg, GameObject origin) {
        if (origin != null && origin instanceof Tower) {
            Tower originTower = (Tower)origin;

            if (originTower.getConfig().strongAgainst.contains(getClass())) {
                dmg *= getManager().getSettings().strongAgainstModifier;
            }

            if (originTower.getConfig().weakAgainst.contains(getClass())) {
                dmg *= getManager().getSettings().weakAgainstModifier;
            }

            originTower.reportDamageInflicted(dmg);
        }

        mHealth -= dmg / mHealthModifier;

        if (mHealth <= 0) {
            getManager().giveCredits(getReward(), true);
            this.remove();
        }
    }

    public void heal(float val) {
        mHealth += val / mHealthModifier;

        if (mHealth > mConfig.health) {
            mHealth = mConfig.health;
        }
    }

    public int getReward() {
        return Math.round(mConfig.reward * mRewardModifier);
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


    protected Vector2 getWayPoint() {
        return mPath.get(mWayPointIndex);
    }

    protected boolean hasWayPoint() {
        return mPath != null && mWayPointIndex < mPath.count();
    }

    public float getProperty(String name) {
        return mConfig.properties.get(name);
    }
}
