package ch.logixisland.anuto.data.enemy;

public class HealerProperties extends EnemyProperties {

    private final HealerSettings mHealerSettings;

    public HealerProperties(EnemySettingsRoot enemySettingsRoot, float healthModifier, float rewardModifier) {
        super(enemySettingsRoot.getHealerSettings(), enemySettingsRoot.getGlobalSettings(), healthModifier, rewardModifier);
        mHealerSettings = enemySettingsRoot.getHealerSettings();
    }

    public float getHealAmount() {
        return mHealerSettings.getHealAmount();
    }

    public float getHealRadius() {
        return mHealerSettings.getHealRadius();
    }

    public float getHealInterval() {
        return mHealerSettings.getHealInterval();
    }

    public float getHealDuration() {
        return mHealerSettings.getHealDuration();
    }
}
