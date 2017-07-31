package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.engine.logic.EntityDependencies;
import ch.logixisland.anuto.util.GenericFactory;
import ch.logixisland.anuto.util.data.EnemyConfig;
import ch.logixisland.anuto.util.data.EnemySettings;

public class EnemyFactory {

    private final EntityDependencies mDependencies;
    private final GenericFactory<Enemy> mFactory;

    private EnemySettings mEnemySettings;

    public EnemyFactory(EntityDependencies dependencies) {
        mDependencies = dependencies;
        mFactory = new GenericFactory<>(EntityDependencies.class, EnemyConfig.class);

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
        Enemy enemy = mFactory.createInstance(name, mDependencies, config);
        enemy.resetHealth(config.getHealth());
        enemy.setReward(config.getReward());
        enemy.setBaseSpeed(config.getSpeed());
        enemy.setMinSpeedModifier(mEnemySettings.getMinSpeedModifier());
        enemy.setStrongAgainst(config.getStrongAgainst());
        enemy.setWeakAgainst(config.getWeakAgainst());
        enemy.setStrongAgainstModifier(mEnemySettings.getStrongAgainstModifier());
        enemy.setWeakAgainstModifier(mEnemySettings.getWeakAgainstModifier());
        return enemy;
    }

}
