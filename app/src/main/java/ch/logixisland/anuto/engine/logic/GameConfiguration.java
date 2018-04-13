package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.setting.enemy.EnemySettings;
import ch.logixisland.anuto.data.setting.tower.TowerSettings;
import ch.logixisland.anuto.data.wave.WaveDescriptorList;

public class GameConfiguration {

    private final GameSettings mGameSettings;
    private final EnemySettings mEnemySettings;
    private final TowerSettings mTowerSettings;
    private final MapDescriptor mMapDescriptor;
    private final WaveDescriptorList mWaveDescriptor;

    public GameConfiguration(GameSettings gameSettings, EnemySettings enemySettings,
                             TowerSettings towerSettings, MapDescriptor mapDescriptor,
                             WaveDescriptorList waveDescriptor) {
        mGameSettings = gameSettings;
        mEnemySettings = enemySettings;
        mTowerSettings = towerSettings;
        mMapDescriptor = mapDescriptor;
        mWaveDescriptor = waveDescriptor;
    }

    public GameSettings getGameSettingsRoot() {
        return mGameSettings;
    }

    public EnemySettings getEnemySettingsRoot() {
        return mEnemySettings;
    }

    public TowerSettings getTowerSettingsRoot() {
        return mTowerSettings;
    }

    public MapDescriptor getMapDescriptorRoot() {
        return mMapDescriptor;
    }

    public WaveDescriptorList getWaveDescriptorRoot() {
        return mWaveDescriptor;
    }
}
