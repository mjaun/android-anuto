package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameObject;

public abstract class AimingTower extends Tower implements GameObject.Listener {
    protected Enemy mTarget;

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

    protected void onTargetLost() {
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasTarget() && getDistanceTo(mTarget.getPosition()) > mRange) {
            onTargetLost();
        }
    }

    @Override
    protected void onRemove() {
        setTarget(null);
    }

    @Override
    public void onRemoveObject(GameObject object) {
        onTargetLost();
    }
}
