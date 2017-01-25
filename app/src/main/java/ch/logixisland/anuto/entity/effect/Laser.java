package ch.logixisland.anuto.entity.effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collection;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class Laser extends Effect {

    private final static float EFFECT_DURATION = 0.5f;
    private final static int ALPHA_START = 180;
    private final static int ALPHA_STEP = (int) (ALPHA_START / (GameEngine.TARGET_FRAME_RATE * EFFECT_DURATION));

    private class LaserDrawable implements Drawable {
        private Paint mPaint;
        private int mAlpha = ALPHA_START;

        public LaserDrawable() {
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
    public float mMaxBounceDist;
    private Enemy mOrigin;
    private Enemy mTarget;
    private Vector2 mTargetPos;
    private Collection<Enemy> mPrevTargets;

    private LaserDrawable mDrawObject;

    public Laser(Entity origin, Vector2 position, Enemy target, float damage) {
        this(origin, position, target, damage, 0, 0);
    }

    public Laser(Entity origin, Vector2 position, Enemy target, float damage, int bounce, float maxBounceDist) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        mTarget = target;
        mTargetPos = target.getPosition();

        mDamage = damage;
        mBounce = bounce;
        mMaxBounceDist = maxBounceDist;

        mDrawObject = new LaserDrawable();
    }

    private Laser(Laser origin, Enemy target) {
        this(origin.getOrigin(), origin.mTarget.getPosition(), target, origin.mDamage, origin.mBounce - 1, origin.mMaxBounceDist);

        mOrigin = origin.mTarget;

        mPrevTargets = origin.mPrevTargets;
        mPrevTargets.add(target);
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

            Enemy enemy = (Enemy) getGameEngine().get(Types.ENEMY)
                    .exclude(mPrevTargets)
                    .min(distanceTo(mTarget.getPosition()));

            if (enemy != null && mTarget.getDistanceTo(enemy) <= mMaxBounceDist) {
                getGameEngine().add(new Laser(this, enemy));
            }
        }

        mTarget.damage(mDamage, getOrigin());
    }

    @Override
    protected void effectEnd() {

    }
}
