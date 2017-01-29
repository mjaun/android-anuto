package ch.logixisland.anuto.entity.tower;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.TowerConfig;

public class TowerFactory {

    private final LevelLoader mLevelLoader;
    private final GenericFactory<Tower> mFactory;

    public TowerFactory(LevelLoader levelLoader) {
        mLevelLoader = levelLoader;
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

    public Tower createTower(String name) {
        TowerConfig config = mLevelLoader.getTowerSettings().getTowerConfig(name);
        return mFactory.createInstance(name, config);
    }

    public Tower createTower(int slot) {
        for (TowerConfig config : mLevelLoader.getTowerSettings().getTowerConfigs()) {
            if (config.getSlot() == slot) {
                return mFactory.createInstance(config.getName(), config);
            }
        }

        return null;
    }

}
