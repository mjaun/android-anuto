package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.engine.logic.entity.Entity;

public interface EntityPersister {
    String getEntityName();
    EntityDescriptor writeDescriptor(Entity entity);
    Entity readDescriptor(EntityDescriptor descriptor);
}
