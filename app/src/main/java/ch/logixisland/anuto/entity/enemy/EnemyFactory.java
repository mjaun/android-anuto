package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.EnemyConfig;

public class EnemyFactory {

    private final LevelLoader mLevelLoader;
    private final GenericFactory<Enemy> mFactory;

    public EnemyFactory(LevelLoader levelLoader) {
        mLevelLoader = levelLoader;

        mFactory = new GenericFactory<>(EnemyConfig.class);
        mFactory.registerClass(Blob.class);
        mFactory.registerClass(Flyer.class);
        mFactory.registerClass(Healer.class);
        mFactory.registerClass(Soldier.class);
        mFactory.registerClass(Sprinter.class);
    }

    public Enemy createEnemy(String name) {
        EnemyConfig config = mLevelLoader.getEnemySettings().getEnemyConfig(name);
        return mFactory.createInstance(name, config);
    }

}
