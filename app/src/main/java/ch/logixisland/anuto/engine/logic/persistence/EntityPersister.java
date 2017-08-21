package ch.logixisland.anuto.engine.logic.persistence;

import java.util.List;

import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.engine.logic.GameEngine;

public interface EntityPersister {
    List<EntityDescriptor> writeDescriptors(GameEngine gameEngine);
    void readDescriptor(GameEngine gameEngine, EntityDescriptor entityDescriptor);
}
