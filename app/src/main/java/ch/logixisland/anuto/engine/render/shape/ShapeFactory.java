package ch.logixisland.anuto.engine.render.shape;

import ch.logixisland.anuto.engine.theme.ThemeManager;

public class ShapeFactory {

    private final ThemeManager mThemeManager;

    public ShapeFactory(ThemeManager themeManager) {
        mThemeManager = themeManager;
    }

    public HealthBar createHealthBar(EntityWithHealth entityWithHealth) {
        return new HealthBar(mThemeManager.getTheme(), entityWithHealth);
    }

    public RangeIndicator createRangeIndicator(EntityWithRange entity) {
        return new RangeIndicator(mThemeManager.getTheme(), entity);
    }

    public LevelIndicator createLevelIndicator(EntityWithLevel entity) {
        return new LevelIndicator(mThemeManager.getTheme(), entity);
    }

}
