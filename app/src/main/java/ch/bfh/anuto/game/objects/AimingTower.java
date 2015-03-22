package ch.bfh.anuto.game.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.game.GameObject;

public abstract class AimingTower extends Tower implements GameObject.Listener {

    public enum Strategy {
        Closest,
        Weakest,
        Strongest
    }

    /*
    ------ Members ------
     */

    protected Enemy mTarget = null;
    protected Strategy mStrategy = Strategy.Closest;
    protected boolean mLockOnTarget = true;

    private List<Enemy> mEnemies = new ArrayList<>();
    private List<Float> mDistances = new ArrayList<>();

    /*
    ------ Methods ------
     */

    @Override
    public void tick() {
        super.tick();

        if (hasTarget() && getDistanceTo(mTarget) > mRange) {
            onTargetLost();
        }

        if (!hasTarget() || !mLockOnTarget) {
            nextTarget();
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

    protected void nextTarget() {
        Iterator<Enemy> enemiesInRange = getEnemiesInRange();

        switch (mStrategy) {
            case Closest:
                setTarget(GameObject.closest(enemiesInRange, mPosition));
                break;

            case Strongest:
                setTarget(Enemy.strongest(enemiesInRange));
                break;

            case Weakest:
                setTarget(Enemy.weakest(enemiesInRange));
                break;
        }
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
