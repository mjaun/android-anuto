package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.engine.logic.map.GameMap;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class GameConfiguration {

    private final KeyValueStore mGameSettings;
    private final GameMap mGameMap;
    private final KeyValueStore mWaveInfos;

    public GameConfiguration(KeyValueStore gameSettings, GameMap gameMap, KeyValueStore waveInfos) {
        mGameSettings = gameSettings;
        mGameMap = gameMap;
        mWaveInfos = waveInfos;
    }

    public KeyValueStore getGameSettings() {
        return mGameSettings;
    }

    public GameMap getGameMap() {
        return mGameMap;
    }

    public KeyValueStore getWaveInfos() {
        return mWaveInfos;
    }

}
