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
import ch.logixisland.anuto.util.math.Vector2;

public class TeleportEffect extends Effect {

    private static final float EFFECT_DURATION = 1f;

    private class TeleportDrawObject extends DrawObject {
        private Paint mPaint;

        public TeleportDrawObject() {
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

    TeleportDrawObject mDrawObject;

    public TeleportEffect(GameObject origin, Vector2 position, Enemy target, float distance) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        target.setEnabled(false);

        mTarget = target;
        mDistance = distance;

        mMoveDirection = target.getDirectionTo(this);
        mMoveStep = target.getDistanceTo(this) / EFFECT_DURATION / GameEngine.TARGET_FRAME_RATE;

        mDrawObject = new TeleportDrawObject();
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

        mTarget.move(mMoveDirection, mMoveStep);
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
