package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AreaEffect;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.util.math.Vector2;

public class HealEffect extends AreaEffect {

    private static final float EFFECT_DURATION = 1f;
    private static final float EFFECT_RANGE = 0.7f;

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
            canvas.drawCircle(getPosition().x, getPosition().y, mRange, mPaint);
        }
    }

    private float mHealAmount;

    private final DrawObject mDrawObject;

    public HealEffect(Vector2 position, float amount) {
        setPosition(position);
        mHealAmount = amount;
        mDuration = EFFECT_DURATION;
        mRange = 0f;

        mDrawObject = new HealDrawObject();
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mDrawObject);
    }

    @Override
    public void tick() {
        super.tick();

        mRange += EFFECT_RANGE / (GameEngine.TARGET_FRAME_RATE * EFFECT_DURATION);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mDrawObject);
    }

    @Override
    protected void enemyEnter(Enemy e) {
        e.heal(mHealAmount);
    }

    @Override
    protected void enemyExit(Enemy e) {

    }
}
