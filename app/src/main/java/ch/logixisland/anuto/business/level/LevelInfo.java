package ch.logixisland.anuto.business.level;

public class LevelInfo {

    private String mLevelId;
    private int mLevelNameId;
    private int mLevelThumbId;
    private int mLevelDataId;

    LevelInfo(String levelId, int levelNameId, int levelThumbId, int levelDataId) {
        mLevelId = levelId;
        mLevelNameId = levelNameId;
        mLevelThumbId = levelThumbId;
        mLevelDataId = levelDataId;
    }

    public String getLevelId() {
        return mLevelId;
    }

    public int getLevelNameId() {
        return mLevelNameId;
    }

    public int getLevelThumbId() {
        return mLevelThumbId;
    }

    public int getLevelDataId() {
        return mLevelDataId;
    }
}
