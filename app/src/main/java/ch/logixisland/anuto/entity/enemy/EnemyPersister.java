package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityPersister;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class EnemyPersister extends EntityPersister {

    @Override
    public KeyValueStore writeEntityData(Entity entity) {
        KeyValueStore data = super.writeEntityData(entity);

        Enemy enemy = (Enemy) entity;
        data.putFloat("health", enemy.getHealth());
        data.putFloat("maxHealth", enemy.getMaxHealth());
        data.putVectorList("wayPoints", enemy.getWayPoints());
        data.putInt("wayPointIndex", enemy.getWayPointIndex());
        data.putInt("waveNumber", enemy.getWaveNumber());
        data.putInt("reward", enemy.getReward());
        data.putBoolean("teleported", enemy.wasTeleported());

        return data;
    }

    @Override
    public void readEntityData(Entity entity, KeyValueStore entityData) {
        super.readEntityData(entity, entityData);

        Enemy enemy = (Enemy) entity;
        enemy.setHealth(entityData.getFloat("health"), entityData.getFloat("maxHealth"));
        enemy.setReward(entityData.getInt("reward"));
        enemy.setWaveNumber(entityData.getInt("waveNumber"));
        enemy.setupPath(entityData.getVectorList("wayPoints"), entityData.getInt("wayPointIndex"));

        if (entityData.getBoolean("teleported")) {
            enemy.finishTeleport();
        }
    }

}
