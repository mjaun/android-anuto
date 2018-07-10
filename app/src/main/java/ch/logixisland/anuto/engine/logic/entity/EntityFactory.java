package ch.logixisland.anuto.engine.logic.entity;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.container.KeyValueStore;

public abstract class EntityFactory {

    private KeyValueStore mGameConfig;

    public void setGameConfig(KeyValueStore gameConfig) {
        mGameConfig = gameConfig;
    }

    public abstract Entity create(GameEngine gameEngine);
    public abstract String getEntityName();

    protected KeyValueStore getGameConfig() {
        return mGameConfig;
    }

    public KeyValueStore getEntitySettings() {
        if (!mGameConfig.getStore("entities").hasKey(getEntityName())) {
            return null;
        }

        return mGameConfig.getStore("entities").getStore(getEntityName());
    }

}
