package ch.logixisland.anuto.engine.logic.entity;

import ch.logixisland.anuto.util.container.KeyValueStore;

public abstract class EntityPersister {

    public KeyValueStore writeEntityData(Entity entity) {
        KeyValueStore entityData = new KeyValueStore();

        entityData.putInt("id", entity.getEntityId());
        entityData.putString("name", entity.getEntityName());
        entityData.putVector("position", entity.getPosition());

        return entityData;
    }

    public void readEntityData(Entity entity, KeyValueStore entityData) {
        if (!entity.getEntityName().equals(entityData.getString("name"))) {
            throw new RuntimeException("Got invalid data!");
        }

        entity.setEntityId(entityData.getInt("id"));
        entity.setPosition(entityData.getVector("position"));
    }

}
