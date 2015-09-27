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

public class HealEffect extends Effect {

    private static final float EFFECT_DURATION = 0.7f;

    private class HealDrawObject extends DrawObject {
        private Paint mPaint;

        public HealDrawObject() {
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

    private DrawObject mDrawObject;

    public HealEffect(GameObject origin, Vector2 position, float amount, float radius) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        mHealAmount = amount;
        mRange = radius;
        mDrawRadius = 0f;

        mDrawObject = new HealDrawObject();
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

        mDrawRadius += mRange / (GameEngine.TARGET_FRAME_RATE * EFFECT_DURATION);
    }

    @Override
    protected void effectBegin() {
        StreamIterator<Enemy> enemies = getGame().get(Enemy.TYPE_ID)
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
