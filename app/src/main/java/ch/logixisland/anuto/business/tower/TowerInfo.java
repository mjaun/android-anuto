package ch.logixisland.anuto.business.tower;

import java.util.List;

import ch.logixisland.anuto.entity.tower.Aimer;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerInfoValue;
import ch.logixisland.anuto.entity.tower.TowerStrategy;

public class TowerInfo {

    private int mValue;
    private int mLevel;
    private int mLevelMax;
    private boolean mEnhanceable;
    private int mEnhanceCost;
    private boolean mUpgradeable;
    private int mUpgradeCost;
    private boolean mSellable;
    private boolean mCanLockTarget;
    private boolean mDoesLockTarget;
    private boolean mHasStrategy;
    private TowerStrategy mStrategy;
    private List<TowerInfoValue> mProperties;

    public TowerInfo(Tower tower, int credits, boolean controlsEnabled) {
        mValue = tower.getValue();
        mLevel = tower.getLevel();
        mLevelMax = tower.getMaxLevel();
        mEnhanceCost = tower.getEnhanceCost();
        mEnhanceable = tower.isEnhanceable() && mEnhanceCost <= credits && controlsEnabled;
        mUpgradeCost = tower.getUpgradeCost();
        mUpgradeable = tower.isUpgradeable() && mUpgradeCost <= credits && controlsEnabled;
        mSellable = controlsEnabled;

        Aimer aimer = tower.getAimer();

        if (aimer != null) {
            mCanLockTarget = true;
            mDoesLockTarget = aimer.doesLockTarget();
            mHasStrategy = true;
            mStrategy = aimer.getStrategy();
        } else {
            mCanLockTarget = false;
            mHasStrategy = false;
        }

        mProperties = tower.getTowerInfoValues();
    }

    public int getValue() {
        return mValue;
    }

    public boolean isSellable() {
        return mSellable;
    }

    public int getLevel() {
        return mLevel;
    }

    public int getLevelMax() {
        return mLevelMax;
    }

    public boolean isEnhanceable() {
        return mEnhanceable;
    }

    public int getEnhanceCost() {
        return mEnhanceCost;
    }

    public boolean isUpgradeable() {
        return mUpgradeable;
    }

    public int getUpgradeCost() {
        return mUpgradeCost;
    }

    public boolean canLockTarget() {
        return mCanLockTarget;
    }

    public boolean doesLockTarget() {
        return mDoesLockTarget;
    }

    public boolean hasStrategy() {
        return mHasStrategy;
    }

    public TowerStrategy getStrategy() {
        return mStrategy;
    }

    public List<TowerInfoValue> getProperties() {
        return mProperties;
    }

}
