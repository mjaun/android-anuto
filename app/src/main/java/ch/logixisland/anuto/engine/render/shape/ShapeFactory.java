package ch.logixisland.anuto.engine.render.shape;

import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.tower.Tower;

public class ShapeFactory {

    private final ThemeManager mThemeManager;

    public ShapeFactory(ThemeManager themeManager) {
        mThemeManager = themeManager;
    }

    public HealthBar createHealthBar(Enemy enemy) {
        return new HealthBar(mThemeManager, enemy);
    }

    public RangeIndicator createRangeIndicator(Tower tower) {
        return new RangeIndicator(mThemeManager, tower);
    }

    public LevelIndicator createLevelIndicator(Tower tower) {
        return new LevelIndicator(mThemeManager, tower);
    }

}
