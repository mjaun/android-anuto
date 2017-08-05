package ch.logixisland.anuto.entity.tower;

import java.util.List;

import ch.logixisland.anuto.data.descriptor.PathDescriptor;
import ch.logixisland.anuto.data.setting.TowerConfig;
import ch.logixisland.anuto.data.setting.TowerSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.GenericFactory;

public class TowerFactory {

    private final GameEngine mGameEngine;
    private final GenericFactory<Tower> mFactory;

    private TowerSettings mTowerSettings;
    private List<PathDescriptor> mPaths;

    public TowerFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
        mFactory = new GenericFactory<>(GameEngine.class, TowerConfig.class);

        mFactory.registerClass(Canon.class);
        mFactory.registerClass(CanonDual.class);
        mFactory.registerClass(CanonMg.class);

        mFactory.registerClass(LaserTower1.class);
        mFactory.registerClass(LaserTower2.class);
        mFactory.registerClass(LaserTower3.class);

        mFactory.registerClass(Mortar.class);
        mFactory.registerClass(MineLayer.class);
        mFactory.registerClass(RocketLauncher.class);

        mFactory.registerClass(GlueTower.class);
        mFactory.registerClass(GlueGun.class);
        mFactory.registerClass(TeleportTower.class);
    }

    public void setTowerSettings(TowerSettings towerSettings) {
        mTowerSettings = towerSettings;
    }

    public void setPaths(List<PathDescriptor> paths) {
        mPaths = paths;
    }

    public int getTowerValue(String name) {
        TowerConfig config = mTowerSettings.getTowerConfig(name);
        return config.getValue();
    }

    public Tower createTower(String name) {
        TowerConfig config = mTowerSettings.getTowerConfig(name);
        Tower tower = mFactory.createInstance(name, mGameEngine, config);
        tower.setPaths(mPaths);
        return tower;
    }

    public Tower createTower(int slot) {
        for (TowerConfig config : mTowerSettings.getTowerConfigs()) {
            if (config.getSlot() == slot) {
                return createTower(config.getName());
            }
        }

        return null;
    }

}
