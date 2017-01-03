package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.ElementMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TowerSettings {

    @ElementMap(entry="tower", key="name", inline=true)
    private final Map<String, TowerConfig> mTowerConfigs = new HashMap<>();

    public TowerConfig getTowerConfig(String name) {
        return mTowerConfigs.get(name);
    }

}
