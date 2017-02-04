package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.EnemyConfig;
import ch.logixisland.anuto.util.data.EnemySettings;

public class EnemyFactory {

    private final GenericFactory<Enemy> mFactory;

    private EnemySettings mEnemySettings;

    public EnemyFactory() {
        mFactory = new GenericFactory<>(EnemyConfig.class);

        mFactory.registerClass(Blob.class);
        mFactory.registerClass(Flyer.class);
        mFactory.registerClass(Healer.class);
        mFactory.registerClass(Soldier.class);
        mFactory.registerClass(Sprinter.class);
    }

    public void setEnemySettings(EnemySettings enemySettings) {
        mEnemySettings = enemySettings;
    }

    public Enemy createEnemy(String name) {
        EnemyConfig config = mEnemySettings.getEnemyConfig(name);
        return mFactory.createInstance(name, config);
    }

}
