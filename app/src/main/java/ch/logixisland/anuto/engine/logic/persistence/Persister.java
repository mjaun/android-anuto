package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.state.GameState;

public interface Persister {
    void resetState();
    void writeState(GameState gameState);
    void readState(GameState gameState);
}
