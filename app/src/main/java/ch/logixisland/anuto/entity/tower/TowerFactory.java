package ch.logixisland.anuto.entity.tower;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.TowerConfig;

public class TowerFactory {

    private final GenericFactory<Tower> mFactory = new GenericFactory<>(Tower.class, TowerConfig.class);
    private final LevelLoader mLevelLoader;

    public TowerFactory(LevelLoader levelLoader) {
        mLevelLoader = levelLoader;
    }

    public Tower createTower(String name) {
        TowerConfig config = mLevelLoader.getTowerSettings().getTowerConfig(name);
        return mFactory.createInstance(name, config);
    }

    public Tower createTower(int slot) {
        for (TowerConfig towerConfig : mLevelLoader.getTowerSettings().getTowerConfigs()) {
            if (towerConfig.getSlot() == slot) {
                return mFactory.createInstance(towerConfig.getName(), towerConfig);
            }
        }

        return null;
    }

}
