package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;

public abstract class TargetedShot extends Shot implements GameObject.Listener {

    /*
    ------ Members ------
     */

    protected Enemy mTarget;

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

        if (hasTarget() && getDistanceTo(mTarget) <= mSpeed / GameEngine.TARGET_FPS) {
            onTargetReached();
        }
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
        onTargetLost();
    }
}
