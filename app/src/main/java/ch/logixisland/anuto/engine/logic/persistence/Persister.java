package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.util.container.KeyValueStore;

public interface Persister {
    void resetState();

    void readState(KeyValueStore gameState);

    void writeState(KeyValueStore gameState);
}
