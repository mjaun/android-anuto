package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityListener;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class Aimer implements EntityListener {

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

    public StreamIterator<Enemy> getPossibleTargets() {
        return mTower.getGameEngine().getEntitiesByType(Types.ENEMY)
                .filter(Entity.inRange(mTower.getPosition(), mTower.getRange()))
                .cast(Enemy.class);
    }

    private void nextTarget() {
        switch (mStrategy) {
            case Closest:
                setTarget(getPossibleTargets().min(Entity.distanceTo(mTower.getPosition())));
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

    @Override
    public void entityRemoved(Entity entity) {
        setTarget(null);
    }
}
