package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collection;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Effect;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.util.math.Vector2;

public class Laser extends Effect {

    private final static float MAX_BOUNCE_DISTANCE = 2f;

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
            canvas.drawLine(getPosition().x, getPosition().y, mTargetPos.x, mTargetPos.y, mPaint);
        }
    }

    private float mDamage;
    private int mBounce;
    private Enemy mOrigin;
    private Enemy mTarget;
    private Vector2 mTargetPos;
    private Collection<Enemy> mPrevTargets;

    private LaserDrawObject mDrawObject;

    public Laser(Vector2 origin, Enemy target, float damage, int bounce) {
        setPosition(origin);

        mTarget = target;
        mTargetPos = target.getPosition();

        mDuration = LASER_VISIBLE_TIME;
        mDamage = damage;
        mBounce = bounce;

        mDrawObject = new LaserDrawObject();
    }

    private Laser(Enemy origin, Enemy target, float damage, int bounce, Collection<Enemy> prevTargets) {
        this(origin.getPosition(), target, damage, bounce);

        mOrigin = origin;

        mPrevTargets = prevTargets;
        mPrevTargets.add(target);
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

        if (mOrigin != null) {
            setPosition(mOrigin.getPosition());
        }

        mTargetPos = mTarget.getPosition();
    }

    @Override
    protected void effectBegin() {
        if (mBounce > 0) {
            if (mPrevTargets == null) {
                mPrevTargets = new ArrayList<>();
                mPrevTargets.add(mTarget);
            }

            Enemy enemy = (Enemy) getGame().get(Enemy.TYPE_ID)
                    .exclude(mPrevTargets)
                    .min(distanceTo(mTarget.getPosition()));

            if (enemy != null && mTarget.getDistanceTo(enemy) <= MAX_BOUNCE_DISTANCE) {
                getGame().add(new Laser(mTarget, enemy, mDamage, mBounce - 1, mPrevTargets));
            }
        }

        mTarget.damage(mDamage);
    }

    @Override
    protected void effectEnd() {

    }
}
