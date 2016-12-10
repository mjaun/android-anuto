package ch.logixisland.anuto.game.render.shape;

import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.render.theme.ThemeManager;

public class ShapeFactory {

    private final ThemeManager mThemeManager;

    public ShapeFactory(ThemeManager themeManager) {
        mThemeManager = themeManager;
    }

    public HealthBar createHealthBar(Enemy enemy) {
        return new HealthBar(mThemeManager, enemy);
    }

    public RangeIndicator createRangeIndicator(Tower tower) {
        return new RangeIndicator(tower);
    }

}
