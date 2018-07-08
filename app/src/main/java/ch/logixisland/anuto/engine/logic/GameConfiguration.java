package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.data.KeyValueStore;
import ch.logixisland.anuto.data.map.GameMap;

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
