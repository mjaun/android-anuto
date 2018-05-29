package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.state.GameState;
import ch.logixisland.anuto.data.state.EntityData;
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
    public void writeDescriptor(GameState gameState) {
        StreamIterator<Entity> iterator = mGameEngine.getAllEntities()
                .filter(Entity.nameEquals(mEntityName));

        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            gameState.addEntityDescriptor(writeEntityDescriptor(entity));
        }
    }

    @Override
    public void readDescriptor(GameState gameState) {
        for (EntityData entityData : gameState.getEntityData()) {
            if (mEntityName.equals(entityData.getName())) {
                mGameEngine.add(readEntityDescriptor(entityData));
            }
        }
    }

    protected abstract EntityData createEntityDescriptor();

    protected EntityData writeEntityDescriptor(Entity entity) {
        EntityData entityData = createEntityDescriptor();

        entityData.setId(entity.getEntityId());
        entityData.setName(entity.getEntityName());
        entityData.setPosition(entity.getPosition());

        return entityData;
    }

    protected Entity readEntityDescriptor(EntityData entityData) {
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
