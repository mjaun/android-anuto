package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.bfh.anuto.game.DrawObject;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.AreaEffect;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.Vector2;

public class LaserEffect extends AreaEffect {

    private final static float LASER_WIDTH = 1.0f;
    private final static float DAMAGE = 100f;

    private final static float LASER_DRAW_WIDTH = 0.1f;
    private final static float LASER_VISIBLE_TIME = 0.5f;

    private class LaserDrawObject extends DrawObject {
        private Paint mPaint;
        private int mAlpha = 180;

        public LaserDrawObject() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(LASER_DRAW_WIDTH);
            mPaint.setColor(Color.RED);
        }

        public void decreaseVisibility() {
            mAlpha -= 10;

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
    private TickTimer mTimer;

    public LaserEffect(Vector2 position, Vector2 laserTo) {
        mPosition.set(position);
        mLaserTo.set(laserTo);
    }

    @Override
    public void init() {
        super.init();

        mDrawObject = new LaserDrawObject();
        mGame.add(mDrawObject);

        mTimer = new TickTimer();
        mTimer.setInterval(LASER_VISIBLE_TIME);
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

        if (mTimer.tick()) {
            this.remove();
        }
    }

    @Override
    protected void applyEffect() {
        StreamIterator<Enemy> enemies = mGame.getGameObjects(Enemy.TYPE_ID)
                .filter(GameObject.onLine(mPosition, mLaserTo, LASER_WIDTH))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(DAMAGE);
        }
    }
}
