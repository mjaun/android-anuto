package ch.logixisland.anuto.game.entity.shot;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.entity.GameObject;

public abstract class HomingShot extends Shot {

    /*
    ------ Listener Implementations ------
     */

    private final GameObject.Listener mTargetListener = new GameObject.Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            if (!mTargetReached) {
                setTarget(null);
                onTargetLost();
            }
        }
    };

    /*
    ------ Members ------
     */

    private Enemy mTarget;
    private boolean mTargetReached;

    /*
    ------ Constructors ------
     */

    protected HomingShot(GameObject origin) {
        super(origin);
    }

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

        if (isEnabled() && mTarget != null &&
                getDistanceTo(mTarget) <= getSpeed() / GameEngine.TARGET_FRAME_RATE) {
            mTargetReached = true;
            onTargetReached();
        }
    }

    public Enemy getTarget() {
        return mTarget;
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

    protected abstract void onTargetReached();

    protected abstract void onTargetLost();
}
