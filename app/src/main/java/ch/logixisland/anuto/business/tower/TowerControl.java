package ch.logixisland.anuto.business.tower;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Aimer;
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

        Plateau plateau = selectedTower.getPlateau();
        selectedTower.remove();

        mScoreBoard.takeCredits(upgradeCost);

        Aimer upgradedTowerAimer = upgradedTower.getAimer();
        Aimer selectedTowerAimer = selectedTower.getAimer();
        if (upgradedTowerAimer != null && selectedTowerAimer != null) {
            upgradedTowerAimer.setLockTarget(selectedTowerAimer.doesLockTarget());
            upgradedTowerAimer.setStrategy(selectedTowerAimer.getStrategy());
        }

        upgradedTower.setPlateau(plateau);
        upgradedTower.setValue(selectedTower.getValue() + upgradeCost);
        upgradedTower.setEnabled(true);
        mGameEngine.add(upgradedTower);
        mTowerSelector.showTowerInfo(upgradedTower);
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
        Aimer selectedTowerAimer = selectedTower.getAimer();

        if (selectedTowerAimer != null) {
            List<TowerStrategy> values = Arrays.asList(TowerStrategy.values());
            int index = values.indexOf(selectedTowerAimer.getStrategy()) + 1;

            if (index >= values.size()) {
                index = 0;
            }

            selectedTowerAimer.setStrategy(values.get(index));
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
        Aimer selectedTowerAimer = selectedTower.getAimer();

        if (selectedTowerAimer != null) {
            boolean lock = selectedTowerAimer.doesLockTarget();
            selectedTowerAimer.setLockTarget(!lock);
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
