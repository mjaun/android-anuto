package ch.logixisland.anuto.engine.theme;

public class Theme {

    private final int mThemeNameId;
    private final int mGameThemeId;
    private final int mMenuThemeId;
    private final int mLevelsThemeId;

    Theme(int themeNameId, int gameThemeId, int menuThemeId, int menuLevelsId) {
        mThemeNameId = themeNameId;
        mGameThemeId = gameThemeId;
        mMenuThemeId = menuThemeId;
        mLevelsThemeId = menuLevelsId;
    }

    public int getThemeNameId() {
        return mThemeNameId;
    }

    public int getGameThemeId() {
        return mGameThemeId;
    }

    int getActivityThemeId(ActivityType type) {
        switch (type) {
            case Game:
                return mGameThemeId;
            case Menu:
                return mMenuThemeId;
            case Levels:
                return mLevelsThemeId;
        }

        throw new RuntimeException("Unknown activity type!");
    }
}
