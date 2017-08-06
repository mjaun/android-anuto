package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.setting.EnemyProperties;
import ch.logixisland.anuto.data.setting.EnemySettings;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class EnemyFactory {

    private final GameEngine mGameEngine;

    private EnemySettings mEnemySettings;
    private GameSettings mGameSettings;

    public EnemyFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void configureFactory(EnemySettings enemySettings, GameSettings gameSettings) {
        mEnemySettings = enemySettings;
        mGameSettings = gameSettings;
    }

    public Enemy createEnemy(String name) {
        EnemyProperties properties = getEnemyProperties(name);
        Enemy enemy = createInstance(name);
        configureEnemy(enemy, properties);
        return enemy;
    }

    public EnemyProperties getEnemyProperties(String name) {
        switch (name) {
            case "soldier":
                return mEnemySettings.getSoldierProperties();
            case "blob":
                return mEnemySettings.getBlobProperties();
            case "sprinter":
                return mEnemySettings.getSprinterProperties();
            case "healer":
                return mEnemySettings.getHealerProperties();
            case "flyer":
                return mEnemySettings.getFlyerProperties();
            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

    private Enemy createInstance(String name) {
        switch (name) {
            case "soldier":
                return new Soldier(mGameEngine, mEnemySettings.getSoldierProperties());
            case "blob":
                return new Blob(mGameEngine, mEnemySettings.getBlobProperties());
            case "sprinter":
                return new Sprinter(mGameEngine, mEnemySettings.getSprinterProperties());
            case "healer":
                return new Healer(mGameEngine, mEnemySettings.getHealerProperties());
            case "flyer":
                return new Flyer(mGameEngine, mEnemySettings.getFlyerProperties());
            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

    private void configureEnemy(Enemy enemy, EnemyProperties properties) {
        enemy.resetHealth(properties.getHealth());
        enemy.setReward(properties.getReward());
        enemy.setBaseSpeed(properties.getSpeed());
        enemy.setMinSpeedModifier(mGameSettings.getMinSpeedModifier());
        enemy.setStrongAgainst(properties.getStrongAgainst());
        enemy.setWeakAgainst(properties.getWeakAgainst());
        enemy.setStrongAgainstModifier(mGameSettings.getStrongAgainstModifier());
        enemy.setWeakAgainstModifier(mGameSettings.getWeakAgainstModifier());
    }

}
