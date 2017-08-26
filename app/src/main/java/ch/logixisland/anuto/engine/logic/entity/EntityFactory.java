package ch.logixisland.anuto.engine.logic.entity;

import ch.logixisland.anuto.engine.logic.GameEngine;

public interface EntityFactory {
    String getEntityName();
    Entity create(GameEngine gameEngine);
}
