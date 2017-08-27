package ch.logixisland.anuto.engine.logic.entity;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.util.container.SafeMultiMap;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EntityStore implements TickListener {

    private final SafeMultiMap<Entity> mEntities = new SafeMultiMap<>();
    private final SparseArray<Entity> mEntityIdMap = new SparseArray<>();
    private final Map<Class<? extends Entity>, Object> mStaticData = new HashMap<>();

    private int mNextEntityId = 1;

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
        int entityId = mNextEntityId++;
        entity.setEntityId(entityId);
        mEntities.add(entity.getEntityType(), entity);
        mEntityIdMap.put(entityId, entity);
        entity.init();
    }

    public void remove(Entity entity) {
        mEntities.remove(entity.getEntityType(), entity);
        mEntityIdMap.remove(entity.getEntityId());
        entity.clean();
    }

    public void clear() {
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
