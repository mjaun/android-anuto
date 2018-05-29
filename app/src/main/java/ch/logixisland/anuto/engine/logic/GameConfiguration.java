package ch.logixisland.anuto.engine.logic;

import java.util.List;

import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.wave.WaveDescriptor;

public class GameConfiguration {

    private final GameSettings mGameSettings;
    private final MapDescriptor mMapDescriptor;
    private final List<WaveDescriptor> mWaveDescriptors;

    public GameConfiguration(GameSettings gameSettings, MapDescriptor mapDescriptor, List<WaveDescriptor> waveDescriptors) {
        mGameSettings = gameSettings;
        mMapDescriptor = mapDescriptor;
        mWaveDescriptors = waveDescriptors;
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public MapDescriptor getMapDescriptor() {
        return mMapDescriptor;
    }

    public List<WaveDescriptor> getWaveDescriptors() {
        return mWaveDescriptors;
    }

}
