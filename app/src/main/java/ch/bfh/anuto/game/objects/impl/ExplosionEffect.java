package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AreaEffect;
import ch.bfh.anuto.game.objects.DrawObject;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.Vector2;

public class ExplosionEffect extends AreaEffect {

    private final static float EXPLOSION_RADIUS = 1.0f;
    private final static float DAMAGE = 400f;

    private final static float EXPLOSION_VISIBLE_TIME = 0.2f;

    private class ExplosionDrawObject extends DrawObject {
        private Paint mPaint;
        private int mAlpha = 180;

        public ExplosionDrawObject() {
            mPaint = new Paint();
            mPaint.setColor(Color.YELLOW);
            mPaint.setAlpha(mAlpha);
        }

        public void decreaseVisibility() {
            mAlpha -= 20;

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
            canvas.drawCircle(mPosition.x, mPosition.y, EXPLOSION_RADIUS, mPaint);
        }
    }

    private ExplosionDrawObject mDrawObject;
    private TickTimer mTimer;

    public ExplosionEffect(Vector2 position) {
        mPosition.set(position);
    }

    @Override
    public void onInit() {
        super.onInit();

        mDrawObject = new ExplosionDrawObject();
        mGame.add(mDrawObject);

        mTimer = new TickTimer();
        mTimer.setInterval(EXPLOSION_VISIBLE_TIME);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mDrawObject);
    }

    @Override
    public void onTick() {
        super.onTick();

        mDrawObject.decreaseVisibility();

        if (mTimer.tick()) {
            this.remove();
        }
    }

    @Override
    protected void applyEffect() {
        StreamIterator<Enemy> enemies = mGame.getGameObjects(Enemy.TYPE_ID)
                .filter(GameObject.inRange(mPosition, EXPLOSION_RADIUS))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(DAMAGE);
        }
    }
}
