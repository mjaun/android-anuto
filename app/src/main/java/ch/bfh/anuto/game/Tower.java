package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Tower extends GameObject implements GameObject.Listener {

    /*
    ------ Constants ------
     */

    public static final int LAYER = 2;

    /*
    ------ Members ------
     */

    protected float mRange = 1000f;
    protected int mReloadTime = 20;

    protected Enemy mTarget;
    protected int mReloadCounter = 0;

    /*
    ------ Methods ------
     */

    public float getRange() {
        return mRange;
    }

    public int getReloadTime() {
        return mReloadTime;
    }

    public Enemy getTarget() {
        return mTarget;
    }

    public boolean hasTarget() {
        return mTarget != null;
    }

    protected void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    protected void nextTarget() {
        setTarget(null);

        for (Enemy enemy : mGame.getEnemies()) {
            if (getDistanceTo(enemy.getPosition()) <= mRange) {
                setTarget(enemy);
                break;
            }
        }
    }

    protected float getDistanceToTarget() {
        return getDistanceTo(mTarget.getPosition());
    }

    protected PointF getDirectionToTarget() {
        return getDirectionTo(mTarget.getPosition());
    }

    protected boolean isReloaded() {
        return mReloadCounter <= 0;
    }

    protected void shoot(Shot shot) {
        mGame.addObject(shot);
        mReloadCounter = mReloadTime;
    }

    protected void onTargetLost() {
        setTarget(null);
    }

    @Override
    protected void onRemove() {
        setTarget(null);
    }

    @Override
    public void onObjectRemove(GameObject object) {
        onTargetLost();
    }

    @Override
    public void tick() {
        if (mReloadCounter > 0) {
            mReloadCounter--;
        }

        if (hasTarget() && getDistanceToTarget() > mRange) {
            onTargetLost();
        }
    }

    @Override
    public int getLayer() {
        return LAYER;
    }
}
