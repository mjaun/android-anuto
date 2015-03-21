package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameObject;

public abstract class TargetedShot extends Shot implements GameObject.Listener {

    /*
    ------ Members ------
     */

    protected GameObject mTarget;

    /*
    ------ Methods ------
     */

    protected void setTarget(GameObject target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    public GameObject getTarget() {
        return mTarget;
    }

    public boolean hasTarget() {
        return mTarget != null;
    }

    protected void onTargetLost() {
        setTarget(null);
    }


    @Override
    protected void onRemove() {
        setTarget(null);
    }

    /*
    ------ GameObject Listener ------
     */

    @Override
    public void onAddObject(GameObject obj) {
    }

    @Override
    public void onRemoveObject(GameObject obj) {
        onTargetLost();
    }
}
