package ch.logixisland.anuto.engine.logic;

import java.util.List;

import ch.logixisland.anuto.data.map.GameMap;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.wave.WaveInfo;

public class GameConfiguration {

    private final GameSettings mGameSettings;
    private final GameMap mMapDescriptor;
    private final List<WaveInfo> mWaveInfos;

    public GameConfiguration(GameSettings gameSettings, GameMap mapDescriptor, List<WaveInfo> waveInfos) {
        mGameSettings = gameSettings;
        mMapDescriptor = mapDescriptor;
        mWaveInfos = waveInfos;
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public GameMap getMapDescriptor() {
        return mMapDescriptor;
    }

    public List<WaveInfo> getWaveInfos() {
        return mWaveInfos;
    }

}
