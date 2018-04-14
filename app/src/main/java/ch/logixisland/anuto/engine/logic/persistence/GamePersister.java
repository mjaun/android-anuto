package ch.logixisland.anuto.engine.logic.persistence;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.GameDescriptor;

public class GamePersister {

    private List<Persister> mPersisterList = new ArrayList<>();

    public void registerPersister(Persister persister) {
        mPersisterList.add(persister);
    }

    public void readDescriptor(GameDescriptor gameDescriptor) {
        for (Persister persister : mPersisterList) {
            persister.readDescriptor(gameDescriptor);
        }
    }

    public void writeDescriptor(GameDescriptor gameDescriptor) {
        for (Persister persister : mPersisterList) {
            persister.writeDescriptor(gameDescriptor);
        }
    }

}
