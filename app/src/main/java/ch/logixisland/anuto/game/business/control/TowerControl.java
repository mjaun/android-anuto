package ch.logixisland.anuto.game.business.control;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.game.business.score.ScoreBoard;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.tower.AimingTower;
import ch.logixisland.anuto.game.entity.tower.Tower;

public class TowerControl implements TowerSelectionListener {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;

    private Tower mSelectedTower;

    public TowerControl(GameEngine gameEngine, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
    }

    @Override
    public void onSelectedTowerChanged(Tower tower) {
        mSelectedTower = tower;
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

        if (mSelectedTower != null && mSelectedTower.isUpgradeable()) {
            if (mSelectedTower.getUpgradeCost() <= mScoreBoard.getCredits()) {
                mScoreBoard.takeCredits(mSelectedTower.getUpgradeCost());
                mSelectedTower.upgrade();
            }
        }
    }

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

        if (mSelectedTower != null && mSelectedTower.isEnhanceable()) {
            if (mSelectedTower.getEnhanceCost() <= mScoreBoard.getCredits()) {
                mScoreBoard.takeCredits(mSelectedTower.getEnhanceCost());
                mSelectedTower.enhance();
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

        if (mSelectedTower instanceof AimingTower) {
            AimingTower tower = (AimingTower) mSelectedTower;

            List<AimingTower.Strategy> values = Arrays.asList(AimingTower.Strategy.values());
            int index = values.indexOf(tower.getStrategy()) + 1;
            if (index >= values.size()) {
                index = 0;
            }
            tower.setStrategy(values.get(index));
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

        if (mSelectedTower instanceof AimingTower) {
            AimingTower tower = (AimingTower) mSelectedTower;
            tower.setLockOnTarget(!tower.doesLockOnTarget());
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

        if (mSelectedTower != null) {
            mScoreBoard.giveCredits(mSelectedTower.getValue(), false);
            mGameEngine.remove(mSelectedTower);
        }
    }

}
