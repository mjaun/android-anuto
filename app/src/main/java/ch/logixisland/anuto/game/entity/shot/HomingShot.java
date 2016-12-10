package ch.logixisland.anuto.game.entity.shot;

import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.EntityListener;
import ch.logixisland.anuto.game.entity.enemy.Enemy;

public abstract class HomingShot extends Shot implements EntityListener {

    private Enemy mTarget;
    private boolean mTargetReached;

    protected HomingShot(Entity origin) {
        super(origin);
    }

    @Override
    public void clean() {
        super.clean();
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (isEnabled() && mTarget != null && getDistanceTo(mTarget) <= getSpeed() / GameEngine.TARGET_FRAME_RATE) {
            mTargetReached = true;
            targetReached();
        }
    }

    public Enemy getTarget() {
        return mTarget;
    }

    public void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;
        mTargetReached = false;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    protected abstract void targetReached();

    protected abstract void targetLost();

    @Override
    public void entityRemoved(Entity obj) {
        if (!mTargetReached) {
            setTarget(null);
            targetLost();
        }
    }
}
