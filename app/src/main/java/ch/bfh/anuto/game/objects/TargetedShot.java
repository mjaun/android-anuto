package ch.bfh.anuto.game.objects;

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
    public void tick() {
        super.tick();

        if (hasTarget() && getDistanceTo(mTarget) <= mSpeed) {
            onTargetReached();
        }
    }

    @Override
    protected void onRemove() {
        setTarget(null);
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

    protected abstract void onTargetReached();

    protected abstract void onTargetLost();

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
