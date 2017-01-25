package ch.logixisland.anuto.entity.effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class HealEffect extends Effect {

    private static final float EFFECT_DURATION = 0.7f;

    private class HealDrawable implements Drawable {
        private Paint mPaint;

        public HealDrawable() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(0.05f);
            mPaint.setColor(Color.BLUE);
            mPaint.setAlpha(70);
        }

        @Override
        public int getLayer() {
            return Layers.SHOT;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(getPosition().x, getPosition().y, mDrawRadius, mPaint);
        }
    }

    private float mRange;
    private float mDrawRadius;
    private float mHealAmount;

    private Drawable mDrawable;

    public HealEffect(Entity origin, Vector2 position, float amount, float radius) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        mHealAmount = amount;
        mRange = radius;
        mDrawRadius = 0f;

        mDrawable = new HealDrawable();
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mDrawable);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mDrawable);
    }

    @Override
    public void tick() {
        super.tick();

        mDrawRadius += mRange / (GameEngine.TARGET_FRAME_RATE * EFFECT_DURATION);
    }

    @Override
    protected void effectBegin() {
        StreamIterator<Enemy> enemies = getGameEngine().get(Types.ENEMY)
                .filter(inRange(getPosition(), mRange))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy e = enemies.next();
            e.heal(mHealAmount * e.getHealthMax());
        }
    }

    @Override
    protected void effectEnd() {

    }
}
