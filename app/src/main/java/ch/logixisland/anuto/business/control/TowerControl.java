package ch.logixisland.anuto.business.control;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.AimingTower;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerFactory;
import ch.logixisland.anuto.entity.tower.TowerStrategy;

public class TowerControl {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final TowerSelector mTowerSelector;
    private final TowerFactory mTowerFactory;

    public TowerControl(GameEngine gameEngine, ScoreBoard scoreBoard, TowerSelector towerSelector,
                        TowerFactory towerFactory) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mTowerSelector = towerSelector;
        mTowerFactory = towerFactory;
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

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower == null || !selectedTower.isUpgradeable()) {
            return;
        }

        int upgradeCost = selectedTower.getUpgradeCost();
        if (upgradeCost > mScoreBoard.getCredits()) {
            return;
        }

        mScoreBoard.takeCredits(upgradeCost);
        Tower upgradedTower = mTowerFactory.createTower(selectedTower.getUpgradeName());

        if (upgradedTower instanceof AimingTower && selectedTower instanceof AimingTower) {
            AimingTower aimingTower = (AimingTower) selectedTower;
            AimingTower aimingUpgraded = (AimingTower) upgradedTower;
            aimingUpgraded.setLockTarget(aimingTower.doesLockTarget());
            aimingUpgraded.setStrategy(aimingTower.getStrategy());
        }

        Plateau plateau = mGameEngine.get(Types.PLATEAU)
                .cast(Plateau.class)
                .filter(Plateau.occupiedBy(selectedTower))
                .first();

        upgradedTower.setValue(selectedTower.getValue() + upgradeCost);
        upgradedTower.setEnabled(true);
        plateau.setOccupant(upgradedTower);
        mGameEngine.add(upgradedTower);
        mTowerSelector.showTowerInfo(upgradedTower);

        selectedTower.remove();
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

            List<TowerStrategy> values = Arrays.asList(TowerStrategy.values());
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
            tower.setLockTarget(!tower.doesLockTarget());
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
            mScoreBoard.giveCredits(selectedTower.getValue(), false);
            mGameEngine.remove(selectedTower);
        }
    }

}
