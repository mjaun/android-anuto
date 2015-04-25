package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameEngine;

public abstract class TargetedShot extends Shot implements GameObject.Listener {

    /*
    ------ Members ------
     */

    protected Enemy mTarget;
    private boolean mReached;

    /*
    ------ Methods ------
     */

    @Override
    public void onClean() {
        super.onClean();
        setTarget(null);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (hasTarget() && getDistanceTo(mTarget) <= mSpeed / GameEngine.TARGET_FPS) {
            mReached = true;
            onTargetReached();
        }
    }


    protected void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;
        mReached = false;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    public boolean hasTarget() {
        return mTarget != null;
    }

    protected abstract void onTargetReached();

    protected abstract void onTargetLost();

    /*
    ------ GameObject Listener ------
     */

    @Override
    public void onObjectAdded(GameObject obj) {
    }

    @Override
    public void onObjectRemoved(GameObject obj) {
        if (!mReached) {
            onTargetLost();
        }
    }
}
