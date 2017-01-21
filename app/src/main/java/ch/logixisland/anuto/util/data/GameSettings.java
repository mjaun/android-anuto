package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;

public class GameSettings {

    @Element(name = "credits")
    private int mCredits;

    @Element(name = "lives")
    private int mLives;

    @Element(name = "ageModifier")
    private float mAgeModifier;

    @Element(name = "minSpeedModifier")
    private float mMinSpeedModifier;

    @Element(name = "weakAgainstModifier")
    private float mWeakAgainstModifier;

    @Element(name = "strongAgainstModifier")
    private float mStrongAgainstModifier;

    @Element(name = "difficultyOffset")
    private float mDifficultyOffset;

    @Element(name = "difficultyLinear")
    private float mDifficultyLinear;

    @Element(name = "difficultyQuadratic")
    private float mDifficultyQuadratic;

    @Element(name = "rewardModifier")
    private float mRewardModifier;

    @Element(name = "rewardRoot")
    private float mRewardRoot;

    @Element(name = "earlyModifier")
    private float mEarlyModifier;

    @Element(name = "earlyRoot")
    private float mEarlyRoot;

    public int getCredits() {
        return mCredits;
    }

    public int getLives() {
        return mLives;
    }

    public float getAgeModifier() {
        return mAgeModifier;
    }

    public float getMinSpeedModifier() {
        return mMinSpeedModifier;
    }

    public float getWeakAgainstModifier() {
        return mWeakAgainstModifier;
    }

    public float getStrongAgainstModifier() {
        return mStrongAgainstModifier;
    }

    public float getDifficultyOffset() {
        return mDifficultyOffset;
    }

    public float getDifficultyLinear() {
        return mDifficultyLinear;
    }

    public float getDifficultyQuadratic() {
        return mDifficultyQuadratic;
    }

    public float getRewardModifier() {
        return mRewardModifier;
    }

    public float getRewardRoot() {
        return mRewardRoot;
    }

    public float getEarlyModifier() {
        return mEarlyModifier;
    }

    public float getEarlyRoot() {
        return mEarlyRoot;
    }
}
