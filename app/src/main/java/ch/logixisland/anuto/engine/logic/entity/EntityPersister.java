package ch.logixisland.anuto.engine.logic.entity;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;
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
    public void resetState(KeyValueStore gameConfig) {

    }

    @Override
    public void writeState(KeyValueStore gameState) {
        StreamIterator<Entity> iterator = mGameEngine.getAllEntities()
                .filter(Entity.nameEquals(mEntityName));

        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            KeyValueStore entityData = writeEntityData(entity);

            if (entityData != null) {
                gameState.appendStore("entities", entityData);
            }
        }
    }

    @Override
    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        for (KeyValueStore entityData : gameState.getStoreList("entities")) {
            if (mEntityName.equals(entityData.getString("name"))) {
                mGameEngine.add(readEntityData(entityData));
            }
        }
    }

    protected KeyValueStore writeEntityData(Entity entity) {
        KeyValueStore entityData = new KeyValueStore();

        entityData.putInt("id", entity.getEntityId());
        entityData.putString("name", entity.getEntityName());
        entityData.putVector("position", entity.getPosition());

        return entityData;
    }

    protected Entity readEntityData(KeyValueStore entityData) {
        Entity entity = mEntityRegistry.createEntity(
                entityData.getString("name"),
                entityData.getInt("id")
        );

        entity.setPosition(entityData.getVector("position"));

        return entity;
    }

    protected GameEngine getGameEngine() {
        return mGameEngine;
    }

    protected EntityRegistry getEntityRegistry() {
        return mEntityRegistry;
    }

}
