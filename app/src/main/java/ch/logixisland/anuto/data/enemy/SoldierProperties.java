package ch.logixisland.anuto.data.enemy;

public class SoldierProperties extends EnemyProperties {
    public SoldierProperties(EnemySettingsRoot enemySettingsRoot, float healthModifier, float rewardModifier) {
        super(enemySettingsRoot.getSoldierSettings(), enemySettingsRoot.getGlobalSettings(), healthModifier, rewardModifier);
    }
}
