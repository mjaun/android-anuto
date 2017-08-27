package ch.logixisland.anuto.engine.logic.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.game.GameDescriptorRoot;

public class GamePersister {

    private List<Persister> mPersisterList = new ArrayList<>();

    public void registerPersister(Persister persister) {
        mPersisterList.add(persister);
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

    }

    public void saveGame(OutputStream outputStream) {
        GameDescriptorRoot gameDescriptor = new GameDescriptorRoot();

        for (Persister persister : mPersisterList) {
            persister.writeDescriptor(gameDescriptor);
        }

        try {
            gameDescriptor.toXml(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("saveGame() failed!", e);
        }
    }

}
