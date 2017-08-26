package ch.logixisland.anuto.engine.logic.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.logixisland.anuto.data.game.EntityDescriptor;
import ch.logixisland.anuto.data.game.GameDescriptorRoot;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class GamePersister {

    private final GameEngine mGameEngine;

    private List<Persister> mPersisterList = new ArrayList<>();
    private Map<String, EntityPersister> mEntityPersisterMap = new HashMap<>();

    public GamePersister(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerPersister(Persister persister) {
        mPersisterList.add(persister);
    }

    public void registerEntityPersister(String name, EntityPersister persister) {
        mEntityPersisterMap.put(persister.getEntityName(), persister);
    }

    public void loadGame(InputStream inputStream) {
        GameDescriptorRoot gameDescriptor;

        try {
            gameDescriptor = GameDescriptorRoot.fromXml(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("loadGame() failed!", e);
        }

        for (Persister persister : mPersisterList) {
            persister.readDescriptor(gameDescriptor);
        }

        for (EntityDescriptor descriptor : gameDescriptor.getEntityDescriptors()) {
            EntityPersister persister = mEntityPersisterMap.get(descriptor.getName());
            mGameEngine.add(persister.readDescriptor(descriptor));
        }
    }

    public void saveGame(OutputStream outputStream) {
        GameDescriptorRoot gameDescriptor = new GameDescriptorRoot();

        for (Persister persister : mPersisterList) {
            persister.writeDescriptor(gameDescriptor);
        }

        StreamIterator<Entity> iterator = mGameEngine.get();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            String name = entity.getEntityName();

            if (name != null) {
                EntityPersister persister = mEntityPersisterMap.get(name);
                gameDescriptor.addEntityDescriptor(persister.writeDescriptor(entity));
            }
        }

        try {
            gameDescriptor.toXml(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("saveGame() failed!", e);
        }
    }

}
