package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.data.entity.TowerDescriptor;
import ch.logixisland.anuto.data.setting.tower.BasicTowerSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityListener;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.entity.enemy.Enemy;

public abstract class AimingTower extends Tower {

    private static TowerStrategy sDefaultStrategy = TowerStrategy.Closest;
    private static boolean sDefaultLockTarget = true;

    private Enemy mTarget = null;
    private TowerStrategy mStrategy = sDefaultStrategy;
    private boolean mLockTarget = sDefaultLockTarget;

    private final TickTimer mUpdateTimer = TickTimer.createInterval(0.1f);

    private final EntityListener mEntityListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity entity) {
            targetLost();
        }
    };

    public static class AimingTowerPersister extends TowerPersister {
        public AimingTowerPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
            super(gameEngine, entityRegistry, entityName);
        }

        @Override
        protected TowerDescriptor writeEntityDescriptor(Entity entity) {
            TowerDescriptor towerDescriptor = super.writeEntityDescriptor(entity);
            AimingTower tower = (AimingTower) entity;
            towerDescriptor.setStrategy(tower.getStrategy().toString());
            towerDescriptor.setLockTarget(tower.doesLockTarget());
            return towerDescriptor;
        }

        @Override
        protected Tower readEntityDescriptor(EntityDescriptor entityDescriptor) {
            AimingTower tower = (AimingTower) super.readEntityDescriptor(entityDescriptor);
            TowerDescriptor towerDescriptor = (TowerDescriptor) entityDescriptor;
            tower.setStrategy(TowerStrategy.valueOf(towerDescriptor.getStrategy()));
            tower.setLockTarget(towerDescriptor.isLockTarget());
            return tower;
        }
    }

    protected AimingTower(GameEngine gameEngine, BasicTowerSettings config) {
        super(gameEngine, config);
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
