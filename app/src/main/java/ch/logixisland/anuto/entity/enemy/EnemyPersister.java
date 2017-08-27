package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.game.EnemyDescriptor;
import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.persistence.EntityPersister;

public class EnemyPersister implements EntityPersister {

    private final String mEntityName;

    public EnemyPersister(String entityName) {
        mEntityName = entityName;
    }

    @Override
    public String getEntityName() {
        return mEntityName;
    }

    @Override
    public EntityDescriptor writeEntityDescriptor(Entity entity, GameEngine gameEngine) {
        Enemy enemy = (Enemy) entity;
        EnemyDescriptor enemyDescriptor = new EnemyDescriptor();

        enemyDescriptor.setName(enemy.getEntityName());
        enemyDescriptor.setPosition(enemy.getPosition());
        enemyDescriptor.setHealth(enemy.getHealth());
        enemyDescriptor.setMaxHealth(enemy.getMaxHealth());
        enemyDescriptor.setWayPoints(enemy.getWayPoints());
        enemyDescriptor.setWayPointIndex(enemy.getWayPointIndex());
        enemyDescriptor.setWaveNumber(enemy.getWaveNumber());
        enemyDescriptor.setReward(enemy.getReward());

        return enemyDescriptor;
    }

    @Override
    public Entity readEntityDescriptor(EntityRegistry entityRegistry, EntityDescriptor entityDescriptor, GameEngine gameEngine) {
        EnemyDescriptor enemyDescriptor = (EnemyDescriptor) entityDescriptor;
        Enemy enemy = (Enemy) entityRegistry.createEntity(entityDescriptor.getName());

        enemy.setHealth(enemyDescriptor.getHealth(), enemyDescriptor.getMaxHealth());
        enemy.setReward(enemyDescriptor.getReward());
        enemy.setPosition(enemyDescriptor.getPosition());
        enemy.setWaveNumber(enemyDescriptor.getWaveNumber());
        enemy.setupPath(enemyDescriptor.getWayPoints(), enemyDescriptor.getWayPointIndex());

        return enemy;
    }

}
