package ch.logixisland.anuto.game;

import android.content.Context;

import ch.logixisland.anuto.game.business.GameManager;
import ch.logixisland.anuto.game.render.SpriteFactory;
import ch.logixisland.anuto.game.theme.ThemeManager;

public class GameFactory {

    private final SpriteFactory mSpriteFactory;
    private final ThemeManager mThemeManager;
    private final GameEngine mGameEngine;
    private final GameManager mGameManager;

    public GameFactory(Context context) {
        mThemeManager = new ThemeManager();
        mSpriteFactory = new SpriteFactory(context.getResources(), mThemeManager);
        mGameEngine = new GameEngine(mThemeManager);
        mGameManager = new GameManager(mGameEngine);
    }

    public SpriteFactory getSpriteFactory() {
        return mSpriteFactory;
    }

    public ThemeManager getThemeManager() {
        return mThemeManager;
    }

    public GameEngine getGameEngine() {
        return mGameEngine;
    }

    public GameManager getGameManager() {
        return mGameManager;
    }
}
