package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Effect;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class LaserStraight extends Effect {

    private final static float LASER_WIDTH = 0.7f;
    private final static float LASER_VISIBLE_TIME = 0.5f;
    private final static int ALPHA_START = 180;
    private final static int ALPHA_STEP = (int)(ALPHA_START / (GameEngine.TARGET_FRAME_RATE * LASER_VISIBLE_TIME));

    private class LaserDrawObject extends DrawObject {
        private Paint mPaint;
        private int mAlpha = ALPHA_START;

        public LaserDrawObject() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(0.1f);
            mPaint.setColor(Color.RED);
        }

        public void decreaseVisibility() {
            mAlpha -= ALPHA_STEP;

            if (mAlpha < 0) {
                mAlpha = 0;
            }

            mPaint.setAlpha(mAlpha);
        }

        @Override
        public int getLayer() {
            return Layers.SHOT;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawLine(getPosition().x, getPosition().y, mLaserTo.x, mLaserTo.y, mPaint);
        }
    }

    private float mDamage;
    private Vector2 mLaserTo;

    private LaserDrawObject mDrawObject;

    public LaserStraight(Vector2 position, Vector2 laserTo, float damage) {
        setPosition(position);
        mLaserTo = new Vector2(laserTo);

        mDamage = damage;
        mDuration = LASER_VISIBLE_TIME;

        mDrawObject = new LaserDrawObject();
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mDrawObject);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mDrawObject);
    }

    @Override
    public void tick() {
        super.tick();

        mDrawObject.decreaseVisibility();
    }

    @Override
    protected void effectBegin() {
        StreamIterator<Enemy> enemies = getGame().get(Enemy.TYPE_ID)
                .filter(onLine(getPosition(), mLaserTo, LASER_WIDTH))
                .cast(Enemy.class)
                .filter(new Predicate<Enemy>() {
                    @Override
                    public boolean apply(Enemy value) {
                        return !(value instanceof  Flyer);
                    }
                });

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(mDamage);
        }
    }

    @Override
    protected void effectEnd() {

    }
}
