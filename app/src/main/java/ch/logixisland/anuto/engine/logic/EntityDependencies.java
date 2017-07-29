package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.util.data.GameSettings;
import ch.logixisland.anuto.util.data.LevelDescriptor;

public interface EntityDependencies {
    GameEngine getGameEngine();
    GameSettings getGameSettings();
    LevelDescriptor getLevelDescriptor();
}
