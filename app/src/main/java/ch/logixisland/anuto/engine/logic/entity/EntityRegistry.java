package ch.logixisland.anuto.engine.logic.entity;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.data.state.GameState;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.persistence.Persister;

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
    public void resetState() {
        mNextEntityId = 0;
    }

    @Override
    public void writeState(GameState gameState) {
        gameState.putInt("nextEntityId", mNextEntityId);
    }

    @Override
    public void readState(GameState gameState) {
        mNextEntityId = gameState.getInt("nextEntityId");
    }
}
