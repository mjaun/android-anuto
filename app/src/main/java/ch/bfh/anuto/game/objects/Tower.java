package ch.bfh.anuto.game.objects;

import android.graphics.PointF;

import ch.bfh.anuto.game.GameObject;

public abstract class Tower extends GameObject implements GameObject.Listener {

    /*
    ------ Constants ------
     */

    public static final int TYPEID = 3;

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

    @Override
    public int getTypeId() {
        return TYPEID;
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


    public float getRange() {
        return mRange;
    }

    public int getReloadTime() {
        return mReloadTime;
    }

    protected boolean isReloaded() {
        return mReloadCounter <= 0;
    }

    protected void shoot(Shot shot) {
        mGame.addObject(shot);
        mReloadCounter = mReloadTime;
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

        for (GameObject obj : mGame.getObjects(Enemy.TYPEID)) {
            if (getDistanceTo(obj.getPosition()) <= mRange) {
                setTarget((Enemy)obj);
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

    protected float getAngleToTarget() {
        return getAngleTo(mTarget.getPosition());
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
}
