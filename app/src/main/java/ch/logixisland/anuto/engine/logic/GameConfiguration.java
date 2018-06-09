package ch.logixisland.anuto.engine.logic;

import java.util.List;

import ch.logixisland.anuto.data.map.GameMap;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.wave.WaveInfo;

public class GameConfiguration {

    private final GameSettings mGameSettings;
    private final GameMap mGameMap;
    private final List<WaveInfo> mWaveInfos;

    public GameConfiguration(GameSettings gameSettings, GameMap gameMap, List<WaveInfo> waveInfos) {
        mGameSettings = gameSettings;
        mGameMap = gameMap;
        mWaveInfos = waveInfos;
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public GameMap getGameMap() {
        return mGameMap;
    }

    public List<WaveInfo> getWaveInfos() {
        return mWaveInfos;
    }

}
