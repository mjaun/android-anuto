package ch.logixisland.anuto.business.tower;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
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
            mGameEngine.post(this::upgradeTower);
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

        Tower upgradedTower = (Tower) mEntityRegistry.createEntity(selectedTower.getUpgradeName());
        mTowerSelector.showTowerInfo(upgradedTower);
        mScoreBoard.takeCredits(upgradeCost);
        Plateau plateau = selectedTower.getPlateau();
        selectedTower.remove();
        upgradedTower.setPlateau(plateau);
        upgradedTower.setValue(selectedTower.getValue() + upgradeCost);
        upgradedTower.setBuilt();
        mGameEngine.add(upgradedTower);

        Aimer upgradedTowerAimer = upgradedTower.getAimer();
        Aimer selectedTowerAimer = selectedTower.getAimer();
        if (upgradedTowerAimer != null && selectedTowerAimer != null) {
            upgradedTowerAimer.setLockTarget(selectedTowerAimer.doesLockTarget());
            upgradedTowerAimer.setStrategy(selectedTowerAimer.getStrategy());
        }
    }

    public void enhanceTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(this::enhanceTower);
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
            mGameEngine.post(this::cycleTowerStrategy);
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower == null) {
            return;
        }

        Aimer selectedTowerAimer = selectedTower.getAimer();
        if (selectedTowerAimer == null) {
            return;
        }

        List<TowerStrategy> values = Arrays.asList(TowerStrategy.values());
        int index = values.indexOf(selectedTowerAimer.getStrategy()) + 1;

        if (index >= values.size()) {
            index = 0;
        }

        selectedTowerAimer.setStrategy(values.get(index));
        mTowerSelector.updateTowerInfo();
    }

    public void toggleLockTarget() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(this::toggleLockTarget);
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower == null) {
            return;
        }

        Aimer selectedTowerAimer = selectedTower.getAimer();
        if (selectedTowerAimer == null) {
            return;
        }

        boolean lock = selectedTowerAimer.doesLockTarget();
        selectedTowerAimer.setLockTarget(!lock);
        mTowerSelector.updateTowerInfo();
    }

    public void sellTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(this::sellTower);
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower != null) {
            mScoreBoard.giveCredits(selectedTower.getValue(), false);
            mGameEngine.remove(selectedTower);
        }
    }

}
