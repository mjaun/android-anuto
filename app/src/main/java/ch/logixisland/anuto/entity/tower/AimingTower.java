package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.EntityListener;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.data.TowerConfig;

public abstract class AimingTower extends Tower {

    private static TowerStrategy sDefaultStrategy = TowerStrategy.Closest;
    private static boolean sDefaultLockTarget = true;

    private Enemy mTarget = null;
    private TowerStrategy mStrategy = sDefaultStrategy;
    private boolean mLockTarget = sDefaultLockTarget;

    private final TickTimer mUpdateTimer = TickTimer.createInterval(0.1f);

    private final EntityListener mEntityListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity obj) {
            targetLost();
        }
    };

    protected AimingTower(TowerConfig config) {
        super(config);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void clean() {
        super.clean();
        setTarget(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (mUpdateTimer.tick()) {
            if (mTarget != null && getDistanceTo(mTarget) > getRange()) {
                targetLost();
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

    protected void setTarget(Enemy target) {
        if (mTarget != null) {
            mTarget.removeListener(mEntityListener);
        }

        mTarget = target;

        if (mTarget != null) {
            mTarget.addListener(mEntityListener);
        }
    }

    private void nextTarget() {
        switch (mStrategy) {
            case Closest:
                setTarget(getPossibleTargets().min(Entity.distanceTo(getPosition())));
                break;

            case Strongest:
                setTarget(getPossibleTargets().max(Enemy.health()));
                break;

            case Weakest:
                setTarget(getPossibleTargets().min(Enemy.health()));
                break;

            case First:
                setTarget(getPossibleTargets().min(Enemy.distanceRemaining()));
                break;

            case Last:
                setTarget(getPossibleTargets().max(Enemy.distanceRemaining()));
        }
    }

    private void targetLost() {
        setTarget(null);
    }

}
