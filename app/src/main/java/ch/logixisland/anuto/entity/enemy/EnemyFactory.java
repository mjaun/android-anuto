package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.setting.EnemyConfig;
import ch.logixisland.anuto.data.setting.EnemySettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.GenericFactory;

public class EnemyFactory {

    private final GameEngine mGameEngine;
    private final GenericFactory<Enemy> mFactory;

    private EnemySettings mEnemySettings;

    public EnemyFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
        mFactory = new GenericFactory<>(GameEngine.class, EnemyConfig.class);

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
        Enemy enemy = mFactory.createInstance(name, mGameEngine, config);
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
