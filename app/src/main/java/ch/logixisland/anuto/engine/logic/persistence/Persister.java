package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.GameDescriptor;

public interface Persister {
    void writeDescriptor(GameDescriptor gameDescriptor);
    void readDescriptor(GameDescriptor gameDescriptor);
}
