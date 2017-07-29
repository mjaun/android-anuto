package ch.logixisland.anuto.engine.logic;

import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.util.container.SafeMultiMap;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EntityStore {
    private final SafeMultiMap<Entity> mEntities = new SafeMultiMap<>();
    private final Map<Class<? extends Entity>, Object> mStaticData = new HashMap<>();

    Object getStaticData(Entity entity) {
        if (!mStaticData.containsKey(entity.getClass())) {
            mStaticData.put(entity.getClass(), entity.initStatic());
        }

        return mStaticData.get(entity.getClass());
    }

    StreamIterator<Entity> get(int typeId) {
        return mEntities.get(typeId).iterator();
    }

    void add(Entity entity) {
        mEntities.add(entity.getType(), entity);
        entity.init();
    }

    void remove(Entity entity) {
        mEntities.remove(entity.getType(), entity);
        entity.clean();
    }

    void clear() {
        for (Entity obj : mEntities) {
            mEntities.remove(obj.getType(), obj);
            obj.clean();
        }

        mStaticData.clear();
    }

    void tick() {
        for (Entity entity : mEntities) {
            entity.tick();
        }
    }
}
