package ch.logixisland.anuto.engine.logic.persistence;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.state.GameState;

public class GamePersister {

    private List<Persister> mPersisterList = new ArrayList<>();

    public void registerPersister(Persister persister) {
        mPersisterList.add(persister);
    }

    public void resetState() {
        for (Persister persister : mPersisterList) {
            persister.resetState();
        }
    }

    public void readState(GameState gameState) {
        for (Persister persister : mPersisterList) {
            persister.readState(gameState);
        }
    }

    public void writeState(GameState gameState) {
        for (Persister persister : mPersisterList) {
            persister.writeState(gameState);
        }
    }
}
