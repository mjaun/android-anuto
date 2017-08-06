package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.setting.EnemySettings;
import ch.logixisland.anuto.data.setting.EnemySettingsRoot;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class EnemyFactory {

    private final GameEngine mGameEngine;

    private EnemySettingsRoot mEnemySettingsRoot;
    private GameSettings mGameSettings;

    public EnemyFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void configureFactory(EnemySettingsRoot enemySettingsRoot, GameSettings gameSettings) {
        mEnemySettingsRoot = enemySettingsRoot;
        mGameSettings = gameSettings;
    }

    public Enemy createEnemy(String name) {
        EnemySettings properties = getEnemyProperties(name);
        Enemy enemy = createInstance(name);
        configureEnemy(enemy, properties);
        return enemy;
    }

    public EnemySettings getEnemyProperties(String name) {
        switch (name) {
            case "soldier":
                return mEnemySettingsRoot.getSoldierProperties();
            case "blob":
                return mEnemySettingsRoot.getBlobProperties();
            case "sprinter":
                return mEnemySettingsRoot.getSprinterProperties();
            case "healer":
                return mEnemySettingsRoot.getHealerProperties();
            case "flyer":
                return mEnemySettingsRoot.getFlyerProperties();
            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

    private Enemy createInstance(String name) {
        switch (name) {
            case "soldier":
                return new Soldier(mGameEngine, mEnemySettingsRoot.getSoldierProperties());
            case "blob":
                return new Blob(mGameEngine, mEnemySettingsRoot.getBlobProperties());
            case "sprinter":
                return new Sprinter(mGameEngine, mEnemySettingsRoot.getSprinterProperties());
            case "healer":
                return new Healer(mGameEngine, mEnemySettingsRoot.getHealerProperties());
            case "flyer":
                return new Flyer(mGameEngine, mEnemySettingsRoot.getFlyerProperties());
            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

    private void configureEnemy(Enemy enemy, EnemySettings properties) {
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
