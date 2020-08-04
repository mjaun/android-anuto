package ch.logixisland.anuto.entity.shot;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.math.Vector2;

public class TargetTracker implements Entity.Listener {

    public interface Listener {
        void targetReached(Enemy target);

        void targetLost(Enemy target);
    }

    private Enemy mTarget;
    private boolean mTargetReached;

    private final Shot mShot;
    private final Listener mListener;

    public TargetTracker(Shot shot, Listener listener) {
        mShot = shot;
        mListener = listener;
    }

    public TargetTracker(Enemy target, Shot shot, Listener listener) {
        mShot = shot;
        mListener = listener;
        setTarget(target);
    }

    public void setTarget(Enemy target) {
        mTarget = target;
        mTargetReached = false;
    }

    Vector2 getTargetDirection() {
        return mShot.getDirectionTo(mTarget);
    }

    public void tick() {
        if (mTargetReached || mTarget == null) {
            return;
        }

        if (mShot.getDistanceTo(mTarget) <= mShot.getSpeed() / GameEngine.TARGET_FRAME_RATE) {
            mTargetReached = true;
            mListener.targetReached(mTarget);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (!mTargetReached) {
            entity.removeListener(this);
            mListener.targetLost(mTarget);
        }
    }
}
