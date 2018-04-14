package ch.logixisland.anuto.business.game;

import ch.logixisland.anuto.data.GameDescriptor;

public interface GameLoaderListener {
    void gameLoaded();
    void gameSaved(GameDescriptor gameDescriptor);
}
