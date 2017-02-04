package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.data.TowerSettings;

public class TowerFactory {

    private final GenericFactory<Tower> mFactory;

    private TowerSettings mTowerSettings;

    public TowerFactory() {
        mFactory = new GenericFactory<>(TowerConfig.class);

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

    public Tower createTower(String name) {
        TowerConfig config = mTowerSettings.getTowerConfig(name);
        return mFactory.createInstance(name, config);
    }

    public Tower createTower(int slot) {
        for (TowerConfig config : mTowerSettings.getTowerConfigs()) {
            if (config.getSlot() == slot) {
                return mFactory.createInstance(config.getName(), config);
            }
        }

        return null;
    }

}
