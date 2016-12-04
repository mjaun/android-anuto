package ch.logixisland.anuto.game.entity.enemy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.render.Drawable;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.data.EnemyConfig;
import ch.logixisland.anuto.game.data.Path;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.vector.Vector2;
import ch.logixisland.anuto.game.render.theme.Theme;


public abstract class Enemy extends Entity {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = Types.ENEMY;

    private static final float HEALTHBAR_WIDTH = 1.0f;
    private static final float HEALTHBAR_HEIGHT = 0.1f;
    private static final float HEALTHBAR_OFFSET = 0.6f;

    /*
    ------ Healthbar Class ------
     */

    private class HealthBar implements Drawable {
        private Paint mHealthBarBg;
        private Paint mHealthBarFg;

        public HealthBar() {
            Theme theme = getThemeManager().getTheme();

            mHealthBarBg = new Paint();
            mHealthBarBg.setColor(theme.getAltBackgroundColor());
            mHealthBarFg = new Paint();
            mHealthBarFg.setColor(Color.GREEN);
        }

        @Override
        public int getLayer() {
            return Layers.ENEMY_HEALTHBAR;
        }

        @Override
        public void draw(Canvas canvas) {
            if (!MathUtils.equals(mHealth, mConfig.getHealth(), 1f)) {
                canvas.save();
                canvas.translate(getPosition().x - HEALTHBAR_WIDTH / 2f, getPosition().y + HEALTHBAR_OFFSET);

                canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
                canvas.drawRect(0, 0, mHealth / mConfig.getHealth() * HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarFg);
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
        mConfig = getGameManager().getLevel().getEnemyConfig(this);
        mBaseSpeed = mConfig.getSpeed();
        mHealth = mConfig.getHealth();

        mHealthBar = new HealthBar();
    }

    /*
    ------ Methods ------
     */

    @Override
    public final int getType() {
        return TYPE_ID;
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
    }

    @Override
    public void tick() {
        super.tick();

        if (isEnabled()) {
            if (!hasWayPoint()) {
                getGameManager().takeLives(1);
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
        float minSpeed = getGameManager().getSettings().getMinSpeedModifier() * getConfigSpeed();

        return Math.max(minSpeed, speed);
    }

    protected float getConfigSpeed() {
        return mConfig.getSpeed();
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
                dmg *= getGameManager().getSettings().getStrongAgainstModifier();
            }

            if (originTower.getConfig().getWeakAgainstEnemies().contains(getClass())) {
                dmg *= getGameManager().getSettings().getWeakAgainstModifier();
            }

            originTower.reportDamageInflicted(dmg);
        }

        mHealth -= dmg / mHealthModifier;

        if (mHealth <= 0) {
            getGameManager().giveCredits(getReward(), true);
            this.remove();
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
