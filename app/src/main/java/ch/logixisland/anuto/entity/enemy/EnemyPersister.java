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
    protected EnemyData createEntityData() {
        return new EnemyData();
    }

    @Override
    protected EnemyData writeEntityData(Entity entity) {
        Enemy enemy = (Enemy) entity;
        EnemyData data = (EnemyData) super.writeEntityData(entity);

        data.setHealth(enemy.getHealth());
        data.setMaxHealth(enemy.getMaxHealth());
        data.setWayPoints(enemy.getWayPoints());
        data.setWayPointIndex(enemy.getWayPointIndex());
        data.setWaveNumber(enemy.getWaveNumber());
        data.setReward(enemy.getReward());

        return data;
    }

    @Override
    protected Enemy readEntityData(EntityData entityData) {
        Enemy enemy = (Enemy) super.readEntityData(entityData);
        EnemyData data = (EnemyData) entityData;

        enemy.setHealth(data.getHealth(), data.getMaxHealth());
        enemy.setReward(data.getReward());
        enemy.setWaveNumber(data.getWaveNumber());
        enemy.setupPath(data.getWayPoints(), data.getWayPointIndex());

        return enemy;
    }

}
