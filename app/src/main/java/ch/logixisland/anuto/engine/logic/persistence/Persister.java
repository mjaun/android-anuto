package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.state.GameState;

public interface Persister {
    void writeDescriptor(GameState gameState);
    void readDescriptor(GameState gameState);
}
