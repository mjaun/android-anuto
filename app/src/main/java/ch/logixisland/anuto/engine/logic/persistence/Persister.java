package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.util.container.KeyValueStore;

public interface Persister {
    void resetState();
    void writeState(KeyValueStore gameState);
    void readState(KeyValueStore gameState);
}
