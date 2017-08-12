package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.setting.enemy.EnemySettingsRoot;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class EnemyFactory {

    private final GameEngine mGameEngine;

    private EnemySettingsRoot mEnemySettingsRoot;

    public EnemyFactory(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void setEnemySettingsRoot(EnemySettingsRoot enemySettingsRoot) {
        mEnemySettingsRoot = enemySettingsRoot;
    }

    public float getEnemyHealth(String name) {
        switch (name) {
            case "soldier":
                return mEnemySettingsRoot.getSoldierSettings().getHealth();

            case "blob":
                return mEnemySettingsRoot.getBlobSettings().getHealth();

            case "sprinter":
                return mEnemySettingsRoot.getSprinterSettings().getHealth();

            case "healer":
                return mEnemySettingsRoot.getHealerSettings().getHealth();

            case "flyer":
                return mEnemySettingsRoot.getFlyerSettings().getHealth();

            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

    public Enemy createEnemy(String name, float healthModifier, float rewardModifier) {
        switch (name) {
            case "soldier":
                return new Soldier(mGameEngine, new EnemyProperties(
                        mEnemySettingsRoot.getSoldierSettings(),
                        mEnemySettingsRoot.getGlobalSettings(),
                        healthModifier, rewardModifier));

            case "blob":
                return new Blob(mGameEngine, new EnemyProperties(
                        mEnemySettingsRoot.getBlobSettings(),
                        mEnemySettingsRoot.getGlobalSettings(),
                        healthModifier, rewardModifier));

            case "sprinter":
                return new Sprinter(mGameEngine, new EnemyProperties(
                        mEnemySettingsRoot.getSprinterSettings(),
                        mEnemySettingsRoot.getGlobalSettings(),
                        healthModifier, rewardModifier));

            case "healer":
                return new Healer(mGameEngine, new HealerProperties(
                        mEnemySettingsRoot.getHealerSettings(),
                        mEnemySettingsRoot.getGlobalSettings(),
                        healthModifier, rewardModifier));

            case "flyer":
                return new Flyer(mGameEngine, new EnemyProperties(
                        mEnemySettingsRoot.getFlyerSettings(),
                        mEnemySettingsRoot.getGlobalSettings(),
                        healthModifier, rewardModifier));

            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

}
