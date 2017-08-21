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

public class GamePersister {

    private final GameEngine mGameEngine;

    private List<Persistable> mPersistableList = new ArrayList<>();
    private Map<String, EntityPersister> mEntityPersisterMap = new HashMap<>();

    public GamePersister(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void registerPersistable(Persistable persistable) {
        mPersistableList.add(persistable);
    }

    public void registerEntityPersister(String name, EntityPersister entityPersister) {
        mEntityPersisterMap.put(name, entityPersister);
    }

    public void loadGame(InputStream inputStream) {
        GameDescriptorRoot gameDescriptor;

        try {
            gameDescriptor = GameDescriptorRoot.fromXml(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("loadGame() failed!", e);
        }

        for (Persistable persistable : mPersistableList) {
            persistable.readDescriptor(gameDescriptor);
        }

        for (EntityDescriptor entityDescriptor : gameDescriptor.getEntityDescriptors()) {
            EntityPersister entityPersister = mEntityPersisterMap.get(entityDescriptor.getName());
            entityPersister.readDescriptor(mGameEngine, entityDescriptor);
        }
    }

    public void saveGame(OutputStream outputStream) {
        GameDescriptorRoot gameDescriptor = new GameDescriptorRoot();

        for (Persistable persistable : mPersistableList) {
            persistable.writeDescriptor(gameDescriptor);
        }

        for (EntityPersister entityPersister : mEntityPersisterMap.values()) {
            gameDescriptor.addEntityDescriptors(entityPersister.writeDescriptors(mGameEngine));
        }

        try {
            gameDescriptor.toXml(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("saveGame() failed!", e);
        }
    }

}
