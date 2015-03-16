package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Shot extends GameObject implements GameObject.Listener {

    /*
    ------ Constants ------
     */

    public static final int LAYER = 4;

    /*
    ------ Members ------
     */

    protected Tower mOwner;
    protected Enemy mTarget;

    /*
    ------ Constructors ------
     */

    public Shot(Tower owner, Enemy target) {
        mOwner = owner;

        setTarget(target);
        setPosition(owner.getPosition());
    }

    /*
    ------ Methods ------
     */

    protected float getDistanceToTarget() {
        return getDistanceTo(mTarget.getPosition());
    }

    protected PointF getDirectionToTarget() {
        return getDirectionTo(mTarget.getPosition());
    }

    protected float getAngleToTarget() {
        return getAngleTo(mTarget.getPosition());
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

    public Enemy getTarget() {
        return mTarget;
    }

    public boolean hasTarget() {
        return mTarget != null;
    }

    public Tower getOwner() {
        return mOwner;
    }

    protected void onTargetLost() {
        setTarget(null);
    }

    @Override
    protected void onRemove() {
        setTarget(null);
    }

    @Override
    public void onObjectRemove(GameObject obj) {
        onTargetLost();
    }

    @Override
    public int getLayer() {
        return LAYER;
    }
}
