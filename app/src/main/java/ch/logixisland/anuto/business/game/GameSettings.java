package ch.logixisland.anuto.business.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameSettings {

    private int mStartCredits;
    private int mStartLives;
    private float mDifficultyModifier;
    private float mDifficultyExponent;
    private float mDifficultyLinear;
    private float mRewardModifier;
    private float mRewardExponent;
    private float mEarlyModifier;
    private float mEarlyExponent;
    private float mMinHealthModifier;
    private float mMinRewardModifier;
    private float mAgeModifier;
    private List<String> mBuildMenuTowerNames;

    public int getStartCredits() {
        return mStartCredits;
    }

    public void setStartCredits(int startCredits) {
        mStartCredits = startCredits;
    }

    public int getStartLives() {
        return mStartLives;
    }

    public void setStartLives(int startLives) {
        mStartLives = startLives;
    }

    public float getDifficultyModifier() {
        return mDifficultyModifier;
    }

    public void setDifficultyModifier(float difficultyModifier) {
        mDifficultyModifier = difficultyModifier;
    }

    public float getDifficultyExponent() {
        return mDifficultyExponent;
    }

    public void setDifficultyExponent(float difficultyExponent) {
        mDifficultyExponent = difficultyExponent;
    }

    public float getDifficultyLinear() {
        return mDifficultyLinear;
    }

    public void setDifficultyLinear(float difficultyLinear) {
        mDifficultyLinear = difficultyLinear;
    }

    public float getRewardModifier() {
        return mRewardModifier;
    }

    public void setRewardModifier(float rewardModifier) {
        mRewardModifier = rewardModifier;
    }

    public float getRewardExponent() {
        return mRewardExponent;
    }

    public void setRewardExponent(float rewardExponent) {
        mRewardExponent = rewardExponent;
    }

    public float getEarlyModifier() {
        return mEarlyModifier;
    }

    public void setEarlyModifier(float earlyModifier) {
        mEarlyModifier = earlyModifier;
    }

    public float getEarlyExponent() {
        return mEarlyExponent;
    }

    public void setEarlyExponent(float earlyExponent) {
        mEarlyExponent = earlyExponent;
    }

    public float getMinHealthModifier() {
        return mMinHealthModifier;
    }

    public void setMinHealthModifier(float minHealthModifier) {
        mMinHealthModifier = minHealthModifier;
    }

    public float getMinRewardModifier() {
        return mMinRewardModifier;
    }

    public void setMinRewardModifier(float minRewardModifier) {
        mMinRewardModifier = minRewardModifier;
    }

    public float getAgeModifier() {
        return mAgeModifier;
    }

    public void setAgeModifier(float ageModifier) {
        mAgeModifier = ageModifier;
    }

    public List<String> getBuildMenuTowerNames() {
        return Collections.unmodifiableList(mBuildMenuTowerNames);
    }

    public void setBuildMenuTowerNames(String... towerNames) {
        mBuildMenuTowerNames = Arrays.asList(towerNames);
    }
}
