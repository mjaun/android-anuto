package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;

public interface EntityPersister {
    String getEntityName();
    EntityDescriptor writeDescriptor(Entity entity);
    Entity readDescriptor(EntityRegistry entityRegistry, EntityDescriptor entityDescriptor);
}
