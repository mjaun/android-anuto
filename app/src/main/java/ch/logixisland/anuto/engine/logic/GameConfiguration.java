package ch.logixisland.anuto.engine.logic;

import java.util.List;

import ch.logixisland.anuto.data.GameDescriptor;
import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.wave.WaveDescriptor;

public class GameConfiguration {

    private final GameSettings mGameSettings;
    private final MapDescriptor mMapDescriptor;
    private final List<WaveDescriptor> mWaveDescriptors;
    private final String mMapId;

    public GameConfiguration(GameDescriptor gameDescriptor) {
        mGameSettings = gameDescriptor.getGameSettings();
        mMapDescriptor = gameDescriptor.getMapDescriptor();
        mWaveDescriptors = gameDescriptor.getWaveDescriptors();
        mMapId = gameDescriptor.getMapId();
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

    public String getMapId() {
        return mMapId;
    }
}
