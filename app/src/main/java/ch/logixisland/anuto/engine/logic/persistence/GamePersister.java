package ch.logixisland.anuto.engine.logic.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import ch.logixisland.anuto.data.game.GameDescriptorRoot;

public class GamePersister {

    private List<Persistable> mPersistableList;

    public void registerPersistable(Persistable persistable) {
        mPersistableList.add(persistable);
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
    }

    public void saveGame(OutputStream outputStream) {
        GameDescriptorRoot gameDescriptor = new GameDescriptorRoot();

        for (Persistable persistable : mPersistableList) {
            persistable.writeDescriptor(gameDescriptor);
        }

        try {
            gameDescriptor.toXml(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("saveGame() failed!", e);
        }
    }

}
