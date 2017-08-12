package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.data.game.EntityDescriptor;

public interface EntityFactory {
    Entity create(GameEngine gameEngine);
    Entity create(GameEngine gameEngine, EntityDescriptor entityDescriptor);
}
