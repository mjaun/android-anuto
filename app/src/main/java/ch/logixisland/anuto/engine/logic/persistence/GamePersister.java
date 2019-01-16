package ch.logixisland.anuto.engine.logic.persistence;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.util.container.KeyValueStore;

public class GamePersister {

    private List<Persister> mPersisterList = new ArrayList<>();

    public void registerPersister(Persister persister) {
        mPersisterList.add(persister);
    }

    public void resetState(KeyValueStore gameConfig) {
        for (Persister persister : mPersisterList) {
            persister.resetState(gameConfig);
        }
    }

    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        for (Persister persister : mPersisterList) {
            persister.readState(gameConfig, gameState);
        }
    }

    public void writeState(KeyValueStore gameState) {
        for (Persister persister : mPersisterList) {
            persister.writeState(gameState);
        }
    }
}
