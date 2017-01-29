package ch.logixisland.anuto.engine.theme;

public class Theme {

    private final int mThemeNameId;
    private final int mGameThemeId;
    private final int mMenuThemeId;

    Theme(int themeNameId, int gameThemeId, int menuThemeId) {
        mThemeNameId = themeNameId;
        mGameThemeId = gameThemeId;
        mMenuThemeId = menuThemeId;
    }

    public int getThemeNameId() {
        return mThemeNameId;
    }

    int getGameThemeId() {
        return mGameThemeId;
    }

    int getActivityThemeId(ActivityType type) {
        switch (type) {
            case Game:
                return mGameThemeId;
            case Menu:
                return mMenuThemeId;
        }

        throw new RuntimeException("Unknown activity type!");
    }
}
