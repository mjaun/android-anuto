package ch.logixisland.anuto.entity.enemy;

import java.util.Collection;

import ch.logixisland.anuto.data.enemy.EnemyGlobalSettings;
import ch.logixisland.anuto.data.enemy.EnemySettings;
import ch.logixisland.anuto.data.enemy.WeaponType;

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

    public float getSpeed() {
        return mEnemySettings.getSpeed();
    }

    public int getReward() {
        return mReward;
    }

    public Collection<WeaponType> getWeakAgainst() {
        return mEnemySettings.getWeakAgainst();
    }

    public Collection<WeaponType> getStrongAgainst() {
        return mEnemySettings.getStrongAgainst();
    }

    public float getMinSpeedModifier() {
        return mGlobalSettings.getMinSpeedModifier();
    }

    public float getWeakAgainstModifier() {
        return mGlobalSettings.getWeakAgainstModifier();
    }

    public float getStrongAgainstModifier() {
        return mGlobalSettings.getStrongAgainstModifier();
    }
}
