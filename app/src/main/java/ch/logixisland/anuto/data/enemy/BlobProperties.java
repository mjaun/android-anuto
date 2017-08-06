package ch.logixisland.anuto.data.enemy;

public class BlobProperties extends EnemyProperties {
    public BlobProperties(EnemySettingsRoot enemySettingsRoot, float healthModifier, float rewardModifier) {
        super(enemySettingsRoot.getBlobSettings(), enemySettingsRoot.getGlobalSettings(), healthModifier, rewardModifier);
    }
}
