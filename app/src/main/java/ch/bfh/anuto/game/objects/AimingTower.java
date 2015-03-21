package ch.bfh.anuto.game.objects;

import java.util.ArrayList;
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


    public Enemy getTarget() {
        return mTarget;
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
        Enemy candidate = null;
        float candidateDistance = 0f;

        getEnemiesInRange(mEnemies, mDistances);

        for (int i = 0; i < mEnemies.size(); i++) {
            if (candidate == null) {
                candidate = mEnemies.get(i);
                candidateDistance = mDistances.get(i);
                continue;
            }

            switch (mStrategy) {
                case Closest:
                    if (mDistances.get(i) < candidateDistance) {
                        candidate = mEnemies.get(i);
                    }
                    break;

                case Weakest:
                    if (mEnemies.get(i).getHealth() < candidate.getHealth()) {
                        candidate = mEnemies.get(i);
                    }
                    break;

                case Strongest:
                    if (mEnemies.get(i).getHealth() > candidate.getHealth()) {
                        candidate = mEnemies.get(i);
                    }
                    break;
            }
        }

        setTarget(candidate);
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
