package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.data.enemy.EnemyGlobalSettings;
import ch.logixisland.anuto.data.enemy.EnemySettings;
import ch.logixisland.anuto.entity.tower.Tower;

public class EnemyProperties {

    private final EnemySettings mEnemySettings;
    private final EnemyGlobalSettings mGlobalSettings;

    private final float mHealth;
    private final int mReward;

    public EnemyProperties(
            EnemySettings enemySettings,
            EnemyGlobalSettings globalSettings,
            float healthModifier,
            float rewardModifier) {
        mEnemySettings = enemySettings;
        mGlobalSettings = globalSettings;

        mHealth = enemySettings.getHealth() * healthModifier;
        mReward = Math.round(enemySettings.getReward() * rewardModifier);
    }

    public float getHealth() {
        return mHealth;
    }

    public int getReward() {
        return mReward;
    }

    public float getSpeed() {
        return mEnemySettings.getSpeed();
    }

    public float getMinSpeedModifier() {
        return mGlobalSettings.getMinSpeedModifier();
    }

    public float applyDamageModifiers(float amount, Tower originTower) {
        if (mEnemySettings.getWeakAgainst().contains(originTower.getWeaponType())) {
            amount *= mGlobalSettings.getWeakAgainstModifier();
        }

        if (mEnemySettings.getStrongAgainst().contains(originTower.getWeaponType())) {
            amount *= mGlobalSettings.getStrongAgainstModifier();
        }

        return amount;
    }
}
