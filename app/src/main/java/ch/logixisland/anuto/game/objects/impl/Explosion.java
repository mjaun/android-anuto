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

public class Explosion extends Effect {

    private final static float EFFECT_DURATION = 0.2f;
    private final static int ALPHA_START = 180;
    private final static int ALPHA_STEP = (int)(ALPHA_START / (GameEngine.TARGET_FRAME_RATE * EFFECT_DURATION));

    private class ExplosionDrawObject extends DrawObject {
        private Paint mPaint;
        private int mAlpha = ALPHA_START;

        public ExplosionDrawObject() {
            mPaint = new Paint();
            mPaint.setColor(Color.YELLOW);
            mPaint.setAlpha(mAlpha);
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
            canvas.drawCircle(getPosition().x, getPosition().y, mRadius, mPaint);
        }
    }

    private float mDamage;
    private float mRadius;

    private ExplosionDrawObject mDrawObject;

    public Explosion(GameObject origin, Vector2 position, float damage, float radius) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        mDamage = damage;
        mRadius = radius;

        mDrawObject = new ExplosionDrawObject();
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
                .filter(inRange(getPosition(), mRadius))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(mDamage, getOrigin());
        }
    }

    @Override
    protected void effectEnd() {

    }
}
