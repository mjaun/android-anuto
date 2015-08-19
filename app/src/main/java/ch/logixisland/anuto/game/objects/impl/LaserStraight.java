package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Effect;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class LaserStraight extends Effect {

    private final static float DAMAGE = 300f;
    private final static float LASER_WIDTH = 0.7f;

    private final static float LASER_DRAW_WIDTH = 0.1f;
    private final static float LASER_VISIBLE_TIME = 0.5f;

    private final static int ALPHA_START = 180;
    private final static int ALPHA_STEP = (int)(ALPHA_START / GameEngine.TARGET_FRAME_RATE / LASER_VISIBLE_TIME);

    private class LaserDrawObject extends DrawObject {
        private Paint mPaint;
        private int mAlpha = ALPHA_START;

        public LaserDrawObject() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(LASER_DRAW_WIDTH);
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
            canvas.drawLine(mPosition.x, mPosition.y, mLaserTo.x, mLaserTo.y, mPaint);
        }
    }

    private final Vector2 mLaserTo = new Vector2();

    private LaserDrawObject mDrawObject;

    public LaserStraight(Vector2 position, Vector2 laserTo) {
        mPosition.set(position);
        mLaserTo.set(laserTo);

        mDrawObject = new LaserDrawObject();

        mDuration = LASER_VISIBLE_TIME;
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mDrawObject);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mDrawObject);
    }

    @Override
    public void tick() {
        super.tick();

        mDrawObject.decreaseVisibility();
    }

    @Override
    protected void effectBegin() {
        StreamIterator<Enemy> enemies = mGame.get(Enemy.TYPE_ID)
                .filter(GameObject.onLine(mPosition, mLaserTo, LASER_WIDTH))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(DAMAGE);
        }
    }

    @Override
    protected void effectEnd() {

    }
}
