package ch.logixisland.anuto.engine.logic.persistence;

import ch.logixisland.anuto.data.game.GameDescriptorRoot;

public interface Persistable {
    void writeDescriptor(GameDescriptorRoot gameDescriptor);
    void readDescriptor(GameDescriptorRoot gameDescriptorRoot);
}
