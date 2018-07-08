package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.KeyValueStore;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityPersister;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;

public class EnemyPersister extends EntityPersister {

    public EnemyPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        super(gameEngine, entityRegistry, entityName);
    }

    @Override
    protected KeyValueStore writeEntityData(Entity entity) {
        Enemy enemy = (Enemy) entity;
        KeyValueStore data = super.writeEntityData(entity);

        data.putFloat("health", enemy.getHealth());
        data.putFloat("maxHealth", enemy.getMaxHealth());
        data.putVectorList("wayPoints", enemy.getWayPoints());
        data.putInt("wayPointIndex", enemy.getWayPointIndex());
        data.putInt("waveNumber", enemy.getWaveNumber());
        data.putInt("reward", enemy.getReward());

        return data;
    }

    @Override
    protected Enemy readEntityData(KeyValueStore entityData) {
        Enemy enemy = (Enemy) super.readEntityData(entityData);

        enemy.setHealth(entityData.getFloat("health"), entityData.getFloat("maxHealth"));
        enemy.setReward(entityData.getInt("reward"));
        enemy.setWaveNumber(entityData.getInt("waveNumber"));
        enemy.setupPath(entityData.getVectorList("wayPoints"), entityData.getInt("wayPointIndex"));

        return enemy;
    }

}
