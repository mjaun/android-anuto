package ch.logixisland.anuto.engine.logic.entity;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.container.KeyValueStore;

public interface EntityFactory {
    String getEntityName();
    Entity create(GameEngine gameEngine, KeyValueStore entitySettings);
}
