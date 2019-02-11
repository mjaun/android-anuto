package ch.logixisland.anuto.engine.logic.entity;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EntityRegistry implements Persister {

    private final GameEngine mGameEngine;
    private final Map<String, EntityFactory> mEntityFactories = new HashMap<>();
    private final Map<String, EntityPersister> mEntityPersisters = new HashMap<>();

    private int mNextEntityId;

    public EntityRegistry(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerEntity(String name, EntityFactory factory, EntityPersister persister) {
        mEntityFactories.put(name, factory);
        mEntityPersisters.put(name, persister);
    }

    public Entity createEntity(String name) {
        Entity entity = mEntityFactories.get(name).create(mGameEngine);
        entity.setEntityId(mNextEntityId++);
        return entity;
    }

    @Override
    public void resetState() {
        mNextEntityId = 1;
    }

    @Override
    public void readState(KeyValueStore gameState) {
        mNextEntityId = gameState.getInt("nextEntityId");

        for (KeyValueStore data : gameState.getStoreList("entities")) {
            String entityName = data.getString("name");
            Entity entity = mEntityFactories.get(entityName).create(mGameEngine);
            mEntityPersisters.get(entityName).readEntityData(entity, data);
            mGameEngine.add(entity);
        }
    }

    @Override
    public void writeState(KeyValueStore gameState) {
        gameState.putInt("nextEntityId", mNextEntityId);

        StreamIterator<Entity> iterator = mGameEngine.getAllEntities();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            EntityPersister persister = mEntityPersisters.get(entity.getEntityName());

            if (persister != null) {
                gameState.appendStore("entities", persister.writeEntityData(entity));
            }
        }
    }
}
