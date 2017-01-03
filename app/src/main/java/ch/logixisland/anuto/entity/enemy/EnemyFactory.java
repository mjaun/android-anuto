package ch.logixisland.anuto.entity.enemy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.EnemyConfig;

public class EnemyFactory {

    private final GenericFactory<Enemy> mFactory = new GenericFactory<>(Enemy.class, EnemyConfig.class);
    private final Map<String, EnemyConfig> mEnemyConfigs = new HashMap<>();

    public void setEnemyConfigs(Collection<EnemyConfig> configs) {
        mEnemyConfigs.clear();

        for (EnemyConfig config : configs) {
            mEnemyConfigs.put(config.getName(), config);
        }
    }

    public Enemy createEnemy(String name) {
        EnemyConfig config = mEnemyConfigs.get(name);
        return mFactory.createInstance(name, config);
    }

}
