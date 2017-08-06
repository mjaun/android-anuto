package ch.logixisland.anuto.data.enemy;

public class SprinterProperties extends EnemyProperties {
    public SprinterProperties(EnemySettingsRoot enemySettingsRoot, float healthModifier, float rewardModifier) {
        super(enemySettingsRoot.getSprinterSettings(), enemySettingsRoot.getGlobalSettings(), healthModifier, rewardModifier);
    }
}
