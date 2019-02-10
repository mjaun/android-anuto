package ch.logixisland.anuto.engine.logic.map;

import ch.logixisland.anuto.util.container.KeyValueStore;

public class EnemyInfo {

    private final String mName;
    private final int mPathIndex;
    private final float mDelay;
    private final float mOffset;

    public EnemyInfo(KeyValueStore data) {
        mName = data.getString("name");
        mPathIndex = data.hasKey("pathIndex") ? data.getInt("pathIndex") : 0;
        mDelay = data.hasKey("delay") ? data.getFloat("delay") : 0;
        mOffset = data.hasKey("offset") ? data.getFloat("offset") : 0;
    }

    public String getName() {
        return mName;
    }

    public int getPathIndex() {
        return mPathIndex;
    }

    public float getDelay() {
        return mDelay;
    }

    public float getOffset() {
        return mOffset;
    }
}
