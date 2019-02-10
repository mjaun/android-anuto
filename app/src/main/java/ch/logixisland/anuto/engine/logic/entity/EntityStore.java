package ch.logixisland.anuto.engine.logic.entity;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.util.container.SafeMultiMap;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EntityStore {

    private final SafeMultiMap<Entity> mEntities = new SafeMultiMap<>();
    private final SparseArray<Entity> mEntityIdMap = new SparseArray<>();
    private final Map<Class<? extends Entity>, Object> mStaticData = new HashMap<>();

    public Object getStaticData(Entity entity) {
        if (!mStaticData.containsKey(entity.getClass())) {
            mStaticData.put(entity.getClass(), entity.initStatic());
        }

        return mStaticData.get(entity.getClass());
    }

    public StreamIterator<Entity> getAll() {
        return mEntities.iterator();
    }

    public StreamIterator<Entity> getByType(int typeId) {
        return mEntities.get(typeId).iterator();
    }

    public Entity getById(int entityId) {
        return mEntityIdMap.get(entityId);
    }

    public void add(Entity entity) {
        mEntities.add(entity.getEntityType(), entity);
        if (entity.getEntityId() > 0) {
            mEntityIdMap.put(entity.getEntityId(), entity);
        }
        entity.init();
    }

    public void remove(Entity entity) {
        mEntities.remove(entity.getEntityType(), entity);
        mEntityIdMap.remove(entity.getEntityId());
        entity.clean();
    }

    public void tick() {
        for (Entity entity : mEntities) {
            entity.tick();
        }
    }

    public void clear() {
        for (Entity entity : mEntities) {
            mEntities.remove(entity.getEntityType(), entity);
            entity.clean();
        }

        mStaticData.clear();
    }
}
