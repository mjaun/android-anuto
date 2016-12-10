package ch.logixisland.anuto.game.business.level;

import ch.logixisland.anuto.game.data.Level;
import ch.logixisland.anuto.game.data.Settings;
import ch.logixisland.anuto.game.engine.GameEngine;

public class LevelLoader {

    private final GameEngine mGameEngine;

    private Level mLevel;

    public LevelLoader(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public Settings getSettings() {
        return mLevel.getSettings();
    }

}
