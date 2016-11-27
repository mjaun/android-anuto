package ch.logixisland.anuto.game.entity.shot;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.enemy.Enemy;

public abstract class HomingShot extends Shot {

    /*
    ------ Listener Implementations ------
     */

    private final Entity.Listener mTargetListener = new Entity.Listener() {
        @Override
        public void onObjectAdded(Entity obj) {

        }

        @Override
        public void onObjectRemoved(Entity obj) {
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

    protected HomingShot(Entity origin) {
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
