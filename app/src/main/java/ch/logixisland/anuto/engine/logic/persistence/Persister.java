package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.game.GameDescriptorRoot;

public interface Persister {
    void writeDescriptor(GameDescriptorRoot gameDescriptor);
    void readDescriptor(GameDescriptorRoot gameDescriptor);
}
