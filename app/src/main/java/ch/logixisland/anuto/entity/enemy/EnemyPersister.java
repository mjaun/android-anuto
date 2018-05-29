package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.state.EnemyData;
import ch.logixisland.anuto.data.state.EntityData;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.EntityPersister;

public class EnemyPersister extends EntityPersister {

    public EnemyPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        super(gameEngine, entityRegistry, entityName);
    }

    @Override
    protected EnemyData createEntityDescriptor() {
        return new EnemyData();
    }

    @Override
    protected EnemyData writeEntityDescriptor(Entity entity) {
        Enemy enemy = (Enemy) entity;
        EnemyData enemyDescriptor = (EnemyData) super.writeEntityDescriptor(entity);

        enemyDescriptor.setHealth(enemy.getHealth());
        enemyDescriptor.setMaxHealth(enemy.getMaxHealth());
        enemyDescriptor.setWayPoints(enemy.getWayPoints());
        enemyDescriptor.setWayPointIndex(enemy.getWayPointIndex());
        enemyDescriptor.setWaveNumber(enemy.getWaveNumber());
        enemyDescriptor.setReward(enemy.getReward());

        return enemyDescriptor;
    }

    @Override
    protected Enemy readEntityDescriptor(EntityData entityData) {
        Enemy enemy = (Enemy) super.readEntityDescriptor(entityData);
        EnemyData enemyDescriptor = (EnemyData) entityData;

        enemy.setHealth(enemyDescriptor.getHealth(), enemyDescriptor.getMaxHealth());
        enemy.setReward(enemyDescriptor.getReward());
        enemy.setWaveNumber(enemyDescriptor.getWaveNumber());
        enemy.setupPath(enemyDescriptor.getWayPoints(), enemyDescriptor.getWayPointIndex());

        return enemy;
    }

}
