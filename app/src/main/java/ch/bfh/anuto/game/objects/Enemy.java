package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.simpleframework.xml.Element;

import ch.bfh.anuto.game.DrawObject;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.TypeIds;
import ch.bfh.anuto.game.data.Path;
import ch.bfh.anuto.util.iterator.Function;
import ch.bfh.anuto.util.math.Vector2;


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
            canvas.translate(mPosition.x - HEALTHBAR_WIDTH/2f, mPosition.y + HEALTHBAR_OFFSET);

            canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
            canvas.drawRect(0, 0, mHealth * HEALTHBAR_WIDTH / mHealthMax, HEALTHBAR_HEIGHT, mHealthBarFg);
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

    /*
    ------ Members -----
     */

    @Element(name="path")
    protected Path mPath = null;
    private int mWayPointIndex = 0;

    protected float mHealth = 100f;
    protected float mHealthMax = 100f;
    protected float mSpeed = 1.0f;

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

        if (!hasWayPoint()) {
            this.remove();
            return;
        }

        if (getDistanceTo(getWayPoint()) < mSpeed / GameEngine.TARGET_FPS) {
            setPosition(getWayPoint());
            nextWayPoint();
        }
        else {
            moveSpeed(getDirectionTo(getWayPoint()), mSpeed);
        }
    }


    protected Vector2 getWayPoint() {
        return mPath.getWayPoints().get(mWayPointIndex);
    }

    protected void nextWayPoint() {
        mWayPointIndex++;
    }

    protected boolean hasWayPoint() {
        return mPath != null && mPath.getWayPoints().size() > mWayPointIndex;
    }


    public void damage(float dmg) {
        mHealth -= dmg;

        if (mHealth <= 0) {
            this.remove();
        }
    }

    public void heal(float val) {
        mHealth += val;
    }
}
