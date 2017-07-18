package ch.logixisland.anuto.business.level;

public class LevelInfo {

    private String mLevelId;
    private int mLevelNameResId;
    private int mLevelDataResId;

    LevelInfo(String levelId, int levelNameResId, int levelDataResId) {
        mLevelId = levelId;
        mLevelNameResId = levelNameResId;
        mLevelDataResId = levelDataResId;
    }

    public String getLevelId() {
        return mLevelId;
    }

    public int getLevelNameResId() {
        return mLevelNameResId;
    }

    public int getLevelDataResId() {
        return mLevelDataResId;
    }
}
