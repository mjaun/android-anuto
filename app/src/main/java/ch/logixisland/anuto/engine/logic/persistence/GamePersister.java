package ch.logixisland.anuto.engine.logic.persistence;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.state.GameState;

public class GamePersister {

    private List<Persister> mPersisterList = new ArrayList<>();

    public void registerPersister(Persister persister) {
        mPersisterList.add(persister);
    }

    public void readDescriptor(GameState gameState) {
        for (Persister persister : mPersisterList) {
            persister.readDescriptor(gameState);
        }
    }

    public void writeDescriptor(GameState gameState) {
        for (Persister persister : mPersisterList) {
            persister.writeDescriptor(gameState);
        }
    }

}
