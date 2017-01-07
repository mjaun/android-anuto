package ch.logixisland.anuto.entity.enemy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.EnemyConfig;

public class EnemyFactory {

    private final GenericFactory<Enemy> mFactory = new GenericFactory<>(Enemy.class, EnemyConfig.class);
    private final LevelLoader mLevelLoader;

    public EnemyFactory(LevelLoader levelLoader) {
        mLevelLoader = levelLoader;
    }

    public Enemy createEnemy(String name) {
        EnemyConfig config = mLevelLoader.getEnemySettings().getEnemyConfig(name);
        return mFactory.createInstance(name, config);
    }

}
