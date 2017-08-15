package ch.logixisland.anuto.business.tower;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.EntityRegistry;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.Message;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.AimingTower;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerStrategy;

public class TowerControl {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final TowerSelector mTowerSelector;
    private final EntityRegistry mEntityRegistry;

    public TowerControl(GameEngine gameEngine, ScoreBoard scoreBoard, TowerSelector towerSelector,
                        EntityRegistry entityRegistry) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
        mTowerSelector = towerSelector;
        mEntityRegistry = entityRegistry;
    }

    public void upgradeTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    upgradeTower();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower == null || !selectedTower.isUpgradeable()) {
            return;
        }

        Tower upgradedTower = (Tower) mEntityRegistry.createEntity(selectedTower.getUpgradeName());

        int upgradeCost = upgradedTower.getValue();
        if (upgradeCost > mScoreBoard.getCredits()) {
            return;
        }

        mScoreBoard.takeCredits(upgradeCost);

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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
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
