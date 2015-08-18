package ch.logixisland.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TypeIds;
import ch.logixisland.anuto.game.data.Path;
import ch.logixisland.anuto.util.iterator.Function;
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
            canvas.save();
            canvas.translate(mPosition.x - HEALTHBAR_WIDTH / 2f, mPosition.y + HEALTHBAR_OFFSET);

            canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
            canvas.drawRect(0, 0, mHealth * HEALTHBAR_WIDTH / mHealthMax, HEALTHBAR_HEIGHT, mHealthBarFg);
            canvas.restore();
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

    @Attribute(name="path", required=false)
    private int mPathIndex;

    @Attribute(name="delay", required=false)
    private float mAddDelay;

    private Path mPath = null;
    private int mWayPointIndex;

    protected float mHealth = 100f;
    protected float mHealthMax = 100f;
    protected float mSpeed = 1.0f;
    protected float mSpeedModifier = 1.0f;

    protected int mReward;

    private HealthBar mHealthBar;

    /*
    ------ Public Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init() {
        super.init();

        mPath = GameManager.getInstance().getLevel().getPaths().get(mPathIndex);
        mPosition.set(mPath.get(0));
        mWayPointIndex = 1;

        mHealth = mHealthMax;

        mHealthBar = new HealthBar();
        mGame.add(mHealthBar);
    }

    @Override
    public void clean() {
        super.clean();
        mGame.remove(mHealthBar);
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled) {
            if (!hasWayPoint()) {
                GameManager.getInstance().takeLives(1);
                this.remove();
                return;
            }

            float stepSize = mSpeed * mSpeedModifier / GameEngine.TARGET_FRAME_RATE;
            if (getDistanceTo(getWayPoint()) < stepSize) {
                setPosition(getWayPoint());
                nextWayPoint();
            } else {
                move(getDirectionTo(getWayPoint()), stepSize);
            }
        }
    }


    protected Vector2 getWayPoint() {
        return mPath.get(mWayPointIndex);
    }

    protected void nextWayPoint() {
        mWayPointIndex++;
    }

    protected boolean hasWayPoint() {
        return mPath != null && mWayPointIndex < mPath.count();
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

    public float getSpeed() {
        return mSpeed;
    }

    public Vector2 getDirection() {
        if (!hasWayPoint()) {
            return null;
        }

        return getDirectionTo(getWayPoint());
    }

    public Vector2 getPositionAfter(float sec) {
        if (mPath == null) {
            return mPosition;
        }

        float distance = sec * mSpeed * mSpeedModifier;
        int index = mWayPointIndex;
        Vector2 position = mPosition.copy();

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

    public void modifySpeed(float f) {
        mSpeedModifier *= f;
    }

    public void sendBack(float dist) {
        int index = mWayPointIndex - 1;
        Vector2 pos = mPosition;

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


    public int getReward() {
        return mReward;
    }

    public void setReward(int reward) {
        mReward = reward;
    }

    public float getHealth() {
        return mHealth;
    }

    public void setHealth(float health) {
        mHealth = health;

        if (mHealth > mHealthMax) {
            mHealthMax = mHealth;
        }
    }

    public float getHealthMax() {
        return mHealthMax;
    }

    public void setHealthMax(float healthMax) {
        mHealthMax = healthMax;
    }

    public void damage(float dmg) {
        mHealth -= dmg;

        if (mHealth <= 0) {
            GameManager.getInstance().giveCredits(mReward);
            this.remove();
        }
    }

    public void heal(float val) {
        mHealth += val;
    }


    public float getAddDelay() {
        return mAddDelay;
    }

    public void setAddDelay(float addDelay) {
        mAddDelay = addDelay;
    }
}
