package ch.logixisland.anuto.engine.logic.entity;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class EntityRegistry {

    private final GameEngine mGameEngine;
    private final Map<String, EntityFactory> mEntityFactories = new HashMap<>();

    public EntityRegistry(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerEntity(EntityFactory factory) {
        mEntityFactories.put(factory.getEntityName(), factory);
    }

    public boolean entityExists(String name) {
        return mEntityFactories.containsKey(name);
    }

    public Entity createEntity(String name) {
        return mEntityFactories.get(name).create(mGameEngine);
    }

}
