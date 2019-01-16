package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.entity.enemy.Enemy;

public class Aimer implements Entity.Listener {

    private static TowerStrategy sDefaultStrategy = TowerStrategy.Closest;
    private static boolean sDefaultLockTarget = true;

    private Enemy mTarget;
    private TowerStrategy mStrategy;
    private boolean mLockTarget;

    private final Tower mTower;
    private final TickTimer mUpdateTimer = TickTimer.createInterval(0.1f);

    public Aimer(Tower tower) {
        mTower = tower;
        mStrategy = sDefaultStrategy;
        mLockTarget = sDefaultLockTarget;
    }

    public void tick() {
        if (mUpdateTimer.tick()) {
            if (mTarget != null && mTower.getDistanceTo(mTarget) > mTower.getRange()) {
                setTarget(null);
            }

            if (mTarget == null || !mLockTarget) {
                nextTarget();
            }
        }
    }

    public TowerStrategy getStrategy() {
        return mStrategy;
    }

    public void setStrategy(TowerStrategy strategy) {
        mStrategy = strategy;
        sDefaultStrategy = strategy;
    }

    public boolean doesLockTarget() {
        return mLockTarget;
    }

    public void setLockTarget(boolean lock) {
        mLockTarget = lock;
        sDefaultLockTarget = lock;
    }

    public Enemy getTarget() {
        return mTarget;
    }

    public void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(this);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(this);
        }
    }

    private void nextTarget() {
        switch (mStrategy) {
            case Closest:
                setTarget(mTower.getPossibleTargets().min(Entity.distanceTo(mTower.getPosition())));
                break;

            case Strongest:
                setTarget(mTower.getPossibleTargets().max(Enemy.health()));
                break;

            case Weakest:
                setTarget(mTower.getPossibleTargets().min(Enemy.health()));
                break;

            case First:
                setTarget(mTower.getPossibleTargets().min(Enemy.distanceRemaining()));
                break;

            case Last:
                setTarget(mTower.getPossibleTargets().max(Enemy.distanceRemaining()));
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        setTarget(null);
    }
}
