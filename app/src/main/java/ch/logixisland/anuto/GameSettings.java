package ch.logixisland.anuto;

import ch.logixisland.anuto.entity.tower.Canon;
import ch.logixisland.anuto.entity.tower.GlueTower;
import ch.logixisland.anuto.entity.tower.Mortar;
import ch.logixisland.anuto.entity.tower.SimpleLaser;

public final class GameSettings {
    private GameSettings() {
    }

    public static final int START_CREDITS = 500;
    public static final int START_LIVES = 20;
    public static final float DIFFICULTY_MODIFIER = 8e-4f;
    public static final float DIFFICULTY_EXPONENT = 1.9f;
    public static final float DIFFICULTY_LINEAR = 20;
    public static final float MIN_HEALTH_MODIFIER = 0.5f;
    public static final float REWARD_MODIFIER = 0.4f;
    public static final float REWARD_EXPONENT = 0.5f;
    public static final float MIN_REWARD_MODIFIER = 1f;
    public static final float EARLY_BONUS_MODIFIER = 3f;
    public static final float EARLY_BONUS_EXPONENT = 0.6f;
    public static final float TOWER_AGE_MODIFIER = 0.97f;
    public static final float WEAK_AGAINST_DAMAGE_MODIFIER = 3.0f;
    public static final float STRONG_AGAINST_DAMAGE_MODIFIER = 0.33f;
    public static final float MIN_SPEED_MODIFIER = 0.05f;

    public static final String[] BUILD_MENU_TOWER_NAMES = {
            Canon.ENTITY_NAME,
            SimpleLaser.ENTITY_NAME,
            Mortar.ENTITY_NAME,
            GlueTower.ENTITY_NAME,
    };

}
