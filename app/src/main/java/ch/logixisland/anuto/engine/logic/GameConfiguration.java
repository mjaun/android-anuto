package ch.logixisland.anuto.engine.logic;

import ch.logixisland.anuto.data.map.MapDescriptorRoot;
import ch.logixisland.anuto.data.setting.GameSettingsRoot;
import ch.logixisland.anuto.data.setting.enemy.EnemySettingsRoot;
import ch.logixisland.anuto.data.setting.tower.TowerSettingsRoot;
import ch.logixisland.anuto.data.wave.WaveDescriptorRoot;

public class GameConfiguration {

    private final GameSettingsRoot mGameSettings;
    private final EnemySettingsRoot mEnemySettings;
    private final TowerSettingsRoot mTowerSettings;
    private final MapDescriptorRoot mMapDescriptor;
    private final WaveDescriptorRoot mWaveDescriptor;

    public GameConfiguration(GameSettingsRoot gameSettings, EnemySettingsRoot enemySettings,
                             TowerSettingsRoot towerSettings, MapDescriptorRoot mapDescriptor,
                             WaveDescriptorRoot waveDescriptor) {
        mGameSettings = gameSettings;
        mEnemySettings = enemySettings;
        mTowerSettings = towerSettings;
        mMapDescriptor = mapDescriptor;
        mWaveDescriptor = waveDescriptor;
    }

    public GameSettingsRoot getGameSettingsRoot() {
        return mGameSettings;
    }

    public EnemySettingsRoot getEnemySettingsRoot() {
        return mEnemySettings;
    }

    public TowerSettingsRoot getTowerSettingsRoot() {
        return mTowerSettings;
    }

    public MapDescriptorRoot getMapDescriptorRoot() {
        return mMapDescriptor;
    }

    public WaveDescriptorRoot getWaveDescriptorRoot() {
        return mWaveDescriptor;
    }
}
