package ch.logixisland.anuto.entity.tower;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.TowerConfig;

public class TowerFactory {

    private final GenericFactory<Tower> mFactory = new GenericFactory<>(Tower.class, TowerConfig.class);
    private final Map<String, TowerConfig> mTowerConfigs = new HashMap<>();

    public void setTowerConfigs(Collection<TowerConfig> configs) {
        mTowerConfigs.clear();

        for (TowerConfig config : configs) {
            mTowerConfigs.put(config.getName(), config);
        }
    }

    public Tower createTower(String name) {
        TowerConfig config = mTowerConfigs.get(name);
        return mFactory.createInstance(name, config);
    }

    public Tower createTower(int slot) {
        for (TowerConfig towerConfig : mTowerConfigs.values()) {
            if (towerConfig.getSlot() == slot) {
                return mFactory.createInstance(towerConfig.getName(), towerConfig);
            }
        }

        return null;
    }

}
