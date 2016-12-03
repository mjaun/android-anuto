package ch.logixisland.anuto.game;

import android.content.Context;

import ch.logixisland.anuto.game.render.SpriteFactory;

public class GameFactory {

    private final SpriteFactory mSpriteFactory;

    public GameFactory(Context context) {
        mSpriteFactory = new SpriteFactory(context.getResources());
    }

    public SpriteFactory getSpriteFactory() {
        return mSpriteFactory;
    }
}
