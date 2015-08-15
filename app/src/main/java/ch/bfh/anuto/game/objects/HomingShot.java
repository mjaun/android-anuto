package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameEngine;

public abstract class HomingShot extends Shot {

    /*
    ------ Members ------
     */

    protected Enemy mTarget;
    private boolean mTargetReached;

    /*
    ------ Listener Implementations ------
     */

    private final Listener mTargetListener = new Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            if (!mTargetReached) {
                onTargetLost();
            }

            obj.removeListener(this);
        }
    };

    /*
    ------ Methods ------
     */

    @Override
    public void clean() {
        super.clean();
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled && hasTarget() && getDistanceTo(mTarget) <= mSpeed / GameEngine.TARGET_FRAME_RATE) {
            mTargetReached = true;
            onTargetReached();
        }
    }


    public void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(mTargetListener);
        }

        mTarget = target;
        mTargetReached = false;

        if (mTarget != null) {
            mTarget.addListener(mTargetListener);
        }
    }

    public boolean hasTarget() {
        return mTarget != null;
    }

    protected abstract void onTargetReached();

    protected abstract void onTargetLost();
}
