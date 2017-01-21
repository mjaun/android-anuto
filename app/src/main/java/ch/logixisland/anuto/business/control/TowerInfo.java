package ch.logixisland.anuto.business.control;

import java.util.List;

import ch.logixisland.anuto.entity.tower.AimingTower;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerProperty;
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
    private List<TowerProperty> mProperties;

    public TowerInfo(Tower tower, int credits, boolean gameOver) {
        mValue = tower.getValue();
        mLevel = tower.getTowerLevel();
        mLevelMax = tower.getTowerLevelMax();
        mEnhanceCost = tower.getEnhanceCost();
        mEnhanceable = tower.isEnhanceable() && mEnhanceCost <= credits && !gameOver;
        mUpgradeCost = tower.getUpgradeCost();
        mUpgradeable = tower.isUpgradeable() && mUpgradeCost <= credits && !gameOver;
        mSellable = !gameOver;

        if (tower instanceof AimingTower) {
            AimingTower aimingTower = (AimingTower) tower;
            mCanLockTarget = true;
            mDoesLockTarget = aimingTower.doesLockTarget();
            mHasStrategy = true;
            mStrategy = aimingTower.getStrategy();
        } else {
            mCanLockTarget = false;
            mHasStrategy = false;
        }

        mProperties = tower.getProperties();
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

    public List<TowerProperty> getProperties() {
        return mProperties;
    }

}
