package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.core.Commit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TowerSettings {

    @ElementMap(entry="tower", key="name", inline=true)
    private final Map<String, TowerConfig> mTowerConfigs = new HashMap<>();

    public TowerConfig getTowerConfig(String name) {
        return mTowerConfigs.get(name);
    }

    public Collection<TowerConfig> getTowerConfigs() {
        return mTowerConfigs.values();
    }

    @Commit
    private void commit() {
        for (TowerConfig config : mTowerConfigs.values()) {
            if (config.getUpgrade() != null) {
                TowerConfig upgradeConfig = mTowerConfigs.get(config.getUpgrade());
                config.setUpgradeCost(upgradeConfig.getValue() - config.getValue());
            }
        }
    }

}
