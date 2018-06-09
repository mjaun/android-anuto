package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.state.EntityData;
import ch.logixisland.anuto.data.state.GameState;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public abstract class EntityPersister implements Persister {

    private final GameEngine mGameEngine;
    private final EntityRegistry mEntityRegistry;
    private final String mEntityName;

    public EntityPersister(GameEngine gameEngine, EntityRegistry entityRegistry, String entityName) {
        mGameEngine = gameEngine;
        mEntityRegistry = entityRegistry;
        mEntityName = entityName;
    }

    @Override
    public void writeState(GameState gameState) {
        StreamIterator<Entity> iterator = mGameEngine.getAllEntities()
                .filter(Entity.nameEquals(mEntityName));

        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            gameState.addEntityData(writeEntityData(entity));
        }
    }

    @Override
    public void readState(GameState gameState) {
        for (EntityData entityData : gameState.getEntityData()) {
            if (mEntityName.equals(entityData.getName())) {
                mGameEngine.add(readEntityData(entityData));
            }
        }
    }

    protected abstract EntityData createEntityData();

    protected EntityData writeEntityData(Entity entity) {
        EntityData entityData = createEntityData();

        entityData.setId(entity.getEntityId());
        entityData.setName(entity.getEntityName());
        entityData.setPosition(entity.getPosition());

        return entityData;
    }

    protected Entity readEntityData(EntityData entityData) {
        Entity entity = mEntityRegistry.createEntity(entityData.getName(), entityData.getId());
        entity.setPosition(entityData.getPosition());
        return entity;
    }

    protected GameEngine getGameEngine() {
        return mGameEngine;
    }

    protected EntityRegistry getEntityRegistry() {
        return mEntityRegistry;
    }

}
