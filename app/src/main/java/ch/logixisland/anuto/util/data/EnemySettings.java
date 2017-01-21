package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Commit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnemySettings {

    @ElementList(entry = "enemy", inline = true)
    private List<EnemyConfig> mEnemyConfigList = new ArrayList<>();
    private Map<String, EnemyConfig> mEnemyConfigMap = new HashMap<>();

    public EnemyConfig getEnemyConfig(String name) {
        return mEnemyConfigMap.get(name);
    }

    @Commit
    private void commit() {
        mEnemyConfigMap = new HashMap<>();
        for (EnemyConfig config : mEnemyConfigList) {
            mEnemyConfigMap.put(config.getName(), config);
        }
    }

}
