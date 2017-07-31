package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.util.data.LevelDescriptor;

public interface EntityDependencies {
    GameEngine getGameEngine();
    LevelDescriptor getLevelDescriptor();
}
