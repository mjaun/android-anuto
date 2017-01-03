package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.ElementMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EnemySettings {

    @ElementMap(entry="enemy", key="name", inline=true)
    private final Map<String, EnemyConfig> mEnemyConfigs = new HashMap<>();

    public EnemyConfig getEnemyConfig(String name) {
        return mEnemyConfigs.get(name);
    }

}
