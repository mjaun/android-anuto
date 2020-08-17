package ch.logixisland.anuto.engine.logic.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class EntityRegistry implements Persister {

    private static class Registration {
        private final int mType;
        private final String mName;
        private final EntityFactory mFactory;
        private final EntityPersister mPersister;

        public Registration(int type, String name, EntityFactory factory, EntityPersister persister) {
            mType = type;
            mName = name;
            mFactory = factory;
            mPersister = persister;
        }
    }

    private final GameEngine mGameEngine;
    private final Map<String, Registration> mRegistrations = new HashMap<>();

    private int mNextEntityId;

    public EntityRegistry(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerEntity(EntityFactory factory, EntityPersister persister) {
        Entity entity = factory.create(mGameEngine);

        mRegistrations.put(entity.getEntityName(), new Registration(
                entity.getEntityType(),
                entity.getEntityName(),
                factory,
                persister
        ));
    }

    public Entity createEntity(String name) {
        Registration registration = mRegistrations.get(name);
        assert registration != null;
        Entity entity = registration.mFactory.create(mGameEngine);
        entity.setEntityId(mNextEntityId++);
        return entity;
    }

    public Set<String> getEntityNamesByType(int type) {
        Set<String> result = new HashSet<>();

        for (Registration registration : mRegistrations.values()) {
            if (registration.mType == type) {
                result.add(registration.mName);
            }
        }

        return result;
    }

    @Override
    public void resetState() {
        mNextEntityId = 1;
    }

    @Override
    public void readState(KeyValueStore gameState) {
        mNextEntityId = gameState.getInt("nextEntityId");

        for (KeyValueStore data : gameState.getStoreList("entities")) {
            Registration registration = mRegistrations.get(data.getString("name"));
            assert registration != null;

            Entity entity = registration.mFactory.create(mGameEngine);
            registration.mPersister.readEntityData(entity, data);
            mGameEngine.add(entity);
        }
    }

    @Override
    public void writeState(KeyValueStore gameState) {
        gameState.putInt("nextEntityId", mNextEntityId);

        StreamIterator<Entity> iterator = mGameEngine.getAllEntities();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            Registration registration = mRegistrations.get(entity.getEntityName());

            if (registration == null) {
                continue;
            }

            EntityPersister persister = registration.mPersister;

            if (persister == null) {
                continue;
            }

            gameState.appendStore("entities", persister.writeEntityData(entity));
        }
    }

}
