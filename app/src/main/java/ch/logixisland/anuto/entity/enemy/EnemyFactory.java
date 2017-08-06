package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.enemy.BlobProperties;
import ch.logixisland.anuto.data.enemy.EnemySettings;
import ch.logixisland.anuto.data.enemy.EnemySettingsRoot;
import ch.logixisland.anuto.data.enemy.FlyerProperties;
import ch.logixisland.anuto.data.enemy.HealerProperties;
import ch.logixisland.anuto.data.enemy.SoldierProperties;
import ch.logixisland.anuto.data.enemy.SprinterProperties;
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

    public EnemySettings getEnemySettings(String name) {
        switch (name) {
            case "soldier":
                return mEnemySettingsRoot.getSoldierSettings();
            case "blob":
                return mEnemySettingsRoot.getBlobSettings();
            case "sprinter":
                return mEnemySettingsRoot.getSprinterSettings();
            case "healer":
                return mEnemySettingsRoot.getHealerSettings();
            case "flyer":
                return mEnemySettingsRoot.getFlyerSettings();
            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

    public Enemy createEnemy(String name, float healthModifier, float rewardModifier) {
        switch (name) {
            case "soldier":
                return new Soldier(mGameEngine, new SoldierProperties(mEnemySettingsRoot, healthModifier, rewardModifier));
            case "blob":
                return new Blob(mGameEngine, new BlobProperties(mEnemySettingsRoot, healthModifier, rewardModifier));
            case "sprinter":
                return new Sprinter(mGameEngine, new SprinterProperties(mEnemySettingsRoot, healthModifier, rewardModifier));
            case "healer":
                return new Healer(mGameEngine, new HealerProperties(mEnemySettingsRoot, healthModifier, rewardModifier));
            case "flyer":
                return new Flyer(mGameEngine, new FlyerProperties(mEnemySettingsRoot, healthModifier, rewardModifier));
            default:
                throw new IllegalArgumentException("Enemy name not known!");
        }
    }

}
