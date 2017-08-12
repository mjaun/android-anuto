package ch.logixisland.anuto.engine.logic;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.data.game.EntityDescriptor;

public class EntityRegistry {

    private final GameEngine mGameEngine;
    private final Map<String, EntityFactory> mEntityFactories = new HashMap<>();

    public EntityRegistry(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerEntity(String name, EntityFactory factory) {
        mEntityFactories.put(name, factory);
    }

    public boolean entityExists(String name) {
        return mEntityFactories.containsKey(name);
    }

    public Entity createEntity(String name) {
        return mEntityFactories.get(name).create(mGameEngine);
    }

    public Entity createEntity(String name, EntityDescriptor entityDescriptor) {
        return mEntityFactories.get(name).create(mGameEngine, entityDescriptor);
    }
}
