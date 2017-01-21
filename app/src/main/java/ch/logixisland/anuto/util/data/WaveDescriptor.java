package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaveDescriptor {

    @ElementList(name = "enemies", entry = "enemy")
    private List<EnemyDescriptor> mEnemies = new ArrayList<>();

    @Element(name = "waveReward", required = false)
    private int mWaveReward = 0;

    @Element(name = "healthModifier", required = false)
    private float mHealthModifier = 1f;

    @Element(name = "rewardModifier", required = false)
    private float mRewardModifier = 1f;

    @Element(name = "extend", required = false)
    private int mExtend = 0;

    @Element(name = "maxExtend", required = false)
    private int mMaxExtend = 0;

    @Element(name = "nextWaveDelay", required = false)
    private float mNextWaveDelay = 10;

    public List<EnemyDescriptor> getEnemies() {
        return Collections.unmodifiableList(mEnemies);
    }

    public int getExtend() {
        return mExtend;
    }

    public float getHealthModifier() {
        return mHealthModifier;
    }

    public int getMaxExtend() {
        return mMaxExtend;
    }

    public float getNextWaveDelay() {
        return mNextWaveDelay;
    }

    public float getRewardModifier() {
        return mRewardModifier;
    }

    public int getWaveReward() {
        return mWaveReward;
    }

}
