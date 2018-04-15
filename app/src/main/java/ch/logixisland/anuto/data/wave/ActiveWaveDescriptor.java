package ch.logixisland.anuto.data.wave;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ActiveWaveDescriptor {

    @Element(name = "waveNumber")
    private int mWaveNumber;

    @Element(name = "waveStartTickCount")
    private int mWaveStartTickCount;

    @Element(name = "extend")
    private int mExtend;

    @Element(name = "waveReward")
    private int mWaveReward;

    @Element(name = "enemyHealthModifier")
    private float mEnemyHealthModifier;

    @Element(name = "enemyRewardModifier")
    private float mEnemyRewardModifier;

    public int getWaveNumber() {
        return mWaveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        mWaveNumber = waveNumber;
    }

    public int getWaveStartTickCount() {
        return mWaveStartTickCount;
    }

    public void setWaveStartTickCount(int waveStartTickCount) {
        mWaveStartTickCount = waveStartTickCount;
    }

    public int getExtend() {
        return mExtend;
    }

    public void setExtend(int extend) {
        mExtend = extend;
    }

    public int getWaveReward() {
        return mWaveReward;
    }

    public void setWaveReward(int waveReward) {
        mWaveReward = waveReward;
    }

    public float getEnemyHealthModifier() {
        return mEnemyHealthModifier;
    }

    public void setEnemyHealthModifier(float enemyHealthModifier) {
        mEnemyHealthModifier = enemyHealthModifier;
    }

    public float getEnemyRewardModifier() {
        return mEnemyRewardModifier;
    }

    public void setEnemyRewardModifier(float enemyRewardModifier) {
        mEnemyRewardModifier = enemyRewardModifier;
    }
}
