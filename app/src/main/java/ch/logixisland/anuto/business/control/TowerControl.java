package ch.logixisland.anuto.business.control;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.AimingTower;
import ch.logixisland.anuto.entity.tower.Tower;

public class TowerControl {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final TowerSelector mTowerSelector;

    public TowerControl(GameEngine gameEngine, ScoreBoard scoreBoard, TowerSelector towerSelector) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mTowerSelector = towerSelector;
    }

    public void upgradeTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    upgradeTower();
                }
            });
            return;
        }

        /*
        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower != null && selectedTower.isUpgradeable()) {
            if (selectedTower.getUpgradeCost() <= mScoreBoard.getCredits()) {
                mScoreBoard.takeCredits(selectedTower.getUpgradeCost());



                Plateau plateau = this.getPlateau();
                Tower upgrade;

                try {
                    upgrade = mConfig.getUpgradeTowerConfig().getTowerClass().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

                int cost = getUpgradeCost();
                upgrade.mValue = this.mValue + cost;

                this.remove();
                upgrade.setPlateau(plateau);
                upgrade.setEnabled(true);
                getGameEngine().add(upgrade);

                return upgrade;



                Tower upgradedTower = selectedTower.upgrade();
                mTowerSelector.showTowerInfo(upgradedTower);
            }
        }
        */
    }

    /*
    @Override
    public Tower upgrade() {
        Tower upgrade = super.upgrade();

        if (upgrade instanceof AimingTower) {
            AimingTower aiming = (AimingTower)upgrade;
            aiming.mStrategy = this.mStrategy;
            aiming.mLockOnTarget = this.mLockOnTarget;
        }

        return upgrade;
    }

    public int getUpgradeCost() {
        if (!isUpgradeable()) {
            return -1;
        }

        return mConfig.getUpgradeTowerConfig().getValue() - mConfig.getValue();
    }
    */

    public void enhanceTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    enhanceTower();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower != null && selectedTower.isEnhanceable()) {
            if (selectedTower.getEnhanceCost() <= mScoreBoard.getCredits()) {
                mScoreBoard.takeCredits(selectedTower.getEnhanceCost());
                selectedTower.enhance();
                mTowerSelector.updateTowerInfo();
            }
        }
    }

    public void cycleTowerStrategy() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    cycleTowerStrategy();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower instanceof AimingTower) {
            AimingTower tower = (AimingTower) selectedTower;

            List<AimingTower.Strategy> values = Arrays.asList(AimingTower.Strategy.values());
            int index = values.indexOf(tower.getStrategy()) + 1;
            if (index >= values.size()) {
                index = 0;
            }
            tower.setStrategy(values.get(index));
            mTowerSelector.updateTowerInfo();
        }
    }

    public void toggleLockTarget() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    toggleLockTarget();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower instanceof AimingTower) {
            AimingTower tower = (AimingTower) selectedTower;
            tower.setLockOnTarget(!tower.doesLockOnTarget());
            mTowerSelector.updateTowerInfo();
        }
    }

    public void sellTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    sellTower();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower != null) {
            mScoreBoard.reimburseCredits(selectedTower.getValue());
            mGameEngine.remove(selectedTower);
        }
    }

}
