package ch.logixisland.anuto.entity.effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TeleportEffect extends Effect {

    private static final float EFFECT_DURATION = 1f;

    private class TeleportDrawable implements Drawable {
        private Paint mPaint;

        public TeleportDrawable() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(0.1f);
            mPaint.setColor(Color.MAGENTA);
            mPaint.setAlpha(70);
        }

        @Override
        public int getLayer() {
            return Layers.SHOT;
        }

        @Override
        public void draw(Canvas canvas) {
            Vector2 target = mTarget.getPosition();
            canvas.drawLine(getPosition().x, getPosition().y, target.x, target.y, mPaint);
        }
    }

    Enemy mTarget;
    float mDistance;
    Vector2 mMoveDirection;
    float mMoveStep;

    TeleportDrawable mDrawObject;

    public TeleportEffect(Entity origin, Vector2 position, Enemy target, float distance) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        target.setEnabled(false);

        mTarget = target;
        mDistance = distance;

        mMoveDirection = target.getDirectionTo(this);
        mMoveStep = target.getDistanceTo(this) / EFFECT_DURATION / GameEngine.TARGET_FRAME_RATE;

        mDrawObject = new TeleportDrawable();
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mDrawObject);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mDrawObject);
    }

    @Override
    public void tick() {
        super.tick();

        mTarget.move(mMoveDirection.copy().mul(mMoveStep));
    }

    @Override
    protected void effectBegin() {

    }

    @Override
    protected void effectEnd() {
        mTarget.sendBack(mDistance);
        mTarget.setEnabled(true);
    }
}
