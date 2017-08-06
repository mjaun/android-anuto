package ch.logixisland.anuto.data.enemy;

public class FlyerProperties extends EnemyProperties {
    public FlyerProperties(EnemySettingsRoot enemySettingsRoot, float healthModifier, float rewardModifier) {
        super(enemySettingsRoot.getFlyerSettings(), enemySettingsRoot.getGlobalSettings(), healthModifier, rewardModifier);
    }
}
