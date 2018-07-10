package ch.logixisland.anuto.engine.logic.entity;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class EntityRegistry implements Persister {

    private final GameEngine mGameEngine;
    private final Map<String, EntityFactory> mEntityFactories = new HashMap<>();

    private int mNextEntityId = 1;

    public EntityRegistry(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerEntity(EntityFactory factory) {
        mEntityFactories.put(factory.getEntityName(), factory);
    }

    public Entity createEntity(String name) {
        return createEntity(name, mNextEntityId++);
    }

    public Entity createEntity(String name, int id) {
        Entity entity = mEntityFactories.get(name).create(mGameEngine);
        entity.setEntityId(id);
        return entity;
    }

    @Override
    public void resetState(KeyValueStore gameConfig) {
        mNextEntityId = 0;

        for (EntityFactory entityFactory : mEntityFactories.values()) {
            entityFactory.setGameConfig(gameConfig);
        }
    }

    @Override
    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        resetState(gameConfig);
        mNextEntityId = gameState.getInt("nextEntityId");
    }

    @Override
    public void writeState(KeyValueStore gameState) {
        gameState.putInt("nextEntityId", mNextEntityId);
    }
}
