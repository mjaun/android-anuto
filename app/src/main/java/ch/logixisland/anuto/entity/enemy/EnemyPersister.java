package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.entity.EnemyDescriptor;
import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.EntityPersister;

public class EnemyPersister extends EntityPersister {

    public EnemyPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        super(gameEngine, entityRegistry, entityName);
    }

    @Override
    protected EnemyDescriptor createEntityDescriptor() {
        return new EnemyDescriptor();
    }

    @Override
    protected EnemyDescriptor writeEntityDescriptor(Entity entity) {
        Enemy enemy = (Enemy) entity;
        EnemyDescriptor enemyDescriptor = (EnemyDescriptor) super.writeEntityDescriptor(entity);

        enemyDescriptor.setHealth(enemy.getHealth());
        enemyDescriptor.setMaxHealth(enemy.getMaxHealth());
        enemyDescriptor.setWayPoints(enemy.getWayPoints());
        enemyDescriptor.setWayPointIndex(enemy.getWayPointIndex());
        enemyDescriptor.setWaveNumber(enemy.getWaveNumber());
        enemyDescriptor.setReward(enemy.getReward());

        return enemyDescriptor;
    }

    @Override
    protected Enemy readEntityDescriptor(EntityDescriptor entityDescriptor) {
        Enemy enemy = (Enemy) super.readEntityDescriptor(entityDescriptor);
        EnemyDescriptor enemyDescriptor = (EnemyDescriptor) entityDescriptor;

        enemy.setHealth(enemyDescriptor.getHealth(), enemyDescriptor.getMaxHealth());
        enemy.setReward(enemyDescriptor.getReward());
        enemy.setWaveNumber(enemyDescriptor.getWaveNumber());
        enemy.setupPath(enemyDescriptor.getWayPoints(), enemyDescriptor.getWayPointIndex());

        return enemy;
    }

}
