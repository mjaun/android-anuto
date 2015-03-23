package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.simpleframework.xml.Element;

import java.util.Iterator;

import ch.bfh.anuto.game.DrawObject;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.data.Path;
import ch.bfh.anuto.util.Function;
import ch.bfh.anuto.util.Iterators;
import ch.bfh.anuto.util.Vector2;


public abstract class Enemy extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 2;
    public static final int LAYER = TYPE_ID * 100;

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
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(mPosition.x - HEALTHBAR_WIDTH/2f, mPosition.y + HEALTHBAR_OFFSET);

            canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
            canvas.drawRect(0, 0, mHealth * HEALTHBAR_WIDTH / mHealthMax, HEALTHBAR_HEIGHT, mHealthBarFg);
            canvas.restore();
        }
    }

    /*
    ------ Static ------
     */

    public static Enemy weakest(Iterator<Enemy> enemies) {
        return Iterators.min(enemies, new Function<Enemy, Float>() {
            @Override
            public Float apply(Enemy input) {
                return input.mHealth;
            }
        });
    }

    public static Enemy strongest(Iterator<Enemy> enemies) {
        return Iterators.max(enemies, new Function<Enemy, Float>() {
            @Override
            public Float apply(Enemy input) {
                return input.mHealth;
            }
        });
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
    ------ Constructors ------
     */

    protected Enemy() {
        mHealthBar = new HealthBar();
    }

    /*
    ------ Public Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init(Resources res) {
        mGame.addDrawObject(mHealthBar, LAYER);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mHealthBar);
    }

    @Override
    public void tick() {
        if (!hasWayPoint()) {
            mGame.removeGameObject(this);
            return;
        }

        if (getDistanceTo(getWayPoint()) < mSpeed) {
            setPosition(getWayPoint());
            nextWayPoint();
        }
        else {
            move(getDirectionTo(getWayPoint()), mSpeed);
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
            mGame.removeGameObject(this);
        }
    }

    public void heal(float val) {
        mHealth += val;
    }
}
