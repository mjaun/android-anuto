package ch.logixisland.anuto.engine.logic;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.util.container.SafeMultiMap;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EntityStore implements TickListener {

    private final SafeMultiMap<Entity> mEntities = new SafeMultiMap<>();
    private final SparseArray<Entity> mEntityIdMap = new SparseArray<>();
    private final Map<Class<? extends Entity>, Object> mStaticData = new HashMap<>();

    private int mNextEntityId = 1;

    Object getStaticData(Entity entity) {
        if (!mStaticData.containsKey(entity.getClass())) {
            mStaticData.put(entity.getClass(), entity.initStatic());
        }

        return mStaticData.get(entity.getClass());
    }

    int nextEntityId() {
        return mNextEntityId++;
    }

    StreamIterator<Entity> get(int typeId) {
        return mEntities.get(typeId).iterator();
    }

    void add(Entity entity) {
        int entityId = mNextEntityId++;
        entity.setEntityId(entityId);
        mEntities.add(entity.getEntityType(), entity);
        mEntityIdMap.put(entityId, entity);
        entity.init();
    }

    void add(Entity entity, int entityId) {
        if (mEntityIdMap.get(entityId) != null) {
            throw new RuntimeException("Entity ID already exists!");
        }

        if (entityId >= mNextEntityId) {
            mNextEntityId = entityId + 1;
        }

        entity.setEntityId(entityId);
    }

    void remove(Entity entity) {
        mEntities.remove(entity.getEntityType(), entity);
        mEntityIdMap.remove(entity.getEntityId());
        entity.clean();
    }

    void clear() {
        for (Entity obj : mEntities) {
            mEntities.remove(obj.getEntityType(), obj);
            obj.clean();
        }

        mStaticData.clear();
    }

    @Override
    public void tick() {
        for (Entity entity : mEntities) {
            entity.tick();
        }
    }
}
