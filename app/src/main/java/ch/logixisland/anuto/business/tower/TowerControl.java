package ch.logixisland.anuto.business.tower;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.business.game.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.entity.EntityTypes;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Aimer;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerStrategy;
import ch.logixisland.anuto.util.iterator.StreamIterator;

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

    private boolean doUpgradeTower(Tower selectedTower, boolean doUpdateTowerInfo) {
        if (selectedTower == null || !selectedTower.isUpgradeable()) {
            return false;
        }

        int upgradeCost = selectedTower.getUpgradeCost();
        if (upgradeCost > mScoreBoard.getCredits()) {
            return false;
        }

        Tower upgradedTower = (Tower) mEntityRegistry.createEntity(selectedTower.getUpgradeName());
        if (doUpdateTowerInfo)
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

        return true;
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

        doUpgradeTower(mTowerSelector.getSelectedTower(), true);
    }

    public void upgradeTowerMax() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    upgradeTowerMax();
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

        int newValue = selectedTower.getValue() + upgradeCost;

        Tower upgradedTower = (Tower) mEntityRegistry.createEntity(selectedTower.getUpgradeName());
        mScoreBoard.takeCredits(upgradeCost);

        if (upgradedTower.isUpgradeable()) {
            upgradeCost = upgradedTower.getUpgradeCost();
            if (upgradeCost <= mScoreBoard.getCredits()) {
                newValue += upgradedTower.getValue() + upgradeCost;

                Tower nextUpgradedTower = (Tower) mEntityRegistry.createEntity(upgradedTower.getUpgradeName());
                mScoreBoard.takeCredits(upgradeCost);
                upgradedTower.remove();
                upgradedTower = nextUpgradedTower;
            }
        }

        mTowerSelector.showTowerInfo(upgradedTower);
        Plateau plateau = selectedTower.getPlateau();
        selectedTower.remove();
        upgradedTower.setPlateau(plateau);
        upgradedTower.setValue(newValue);
        upgradedTower.setBuilt();
        mGameEngine.add(upgradedTower);

        Aimer upgradedTowerAimer = upgradedTower.getAimer();
        Aimer selectedTowerAimer = selectedTower.getAimer();
        if (upgradedTowerAimer != null && selectedTowerAimer != null) {
            upgradedTowerAimer.setLockTarget(selectedTowerAimer.doesLockTarget());
            upgradedTowerAimer.setStrategy(selectedTowerAimer.getStrategy());
        }
    }

    public void relayUpgradeTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    relayUpgradeTower();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        //int thisLevel = selectedTower.getLevel();
        //if (!doUpgradeTower(selectedTower, true)) return;
        doUpgradeTower(selectedTower, true);

        StreamIterator<Tower> allOthers = mGameEngine
                .getEntitiesByType(EntityTypes.TOWER)
                .filter(selectedTower.getClass())
                .filter(Tower.isNotThis(selectedTower))
                //.filter(Tower.isThisLevel(thisLevel))
                .cast(Tower.class);

        while (allOthers.hasNext())
            doUpgradeTower(allOthers.next(), false);
    }

    private boolean doEnhanceTower(Tower selectedTower, boolean doUpdateTowerInfo) {
        if (selectedTower != null && selectedTower.isEnhanceable()) {
            if (selectedTower.getEnhanceCost() <= mScoreBoard.getCredits()) {
                mScoreBoard.takeCredits(selectedTower.getEnhanceCost());
                selectedTower.enhance();
                if (doUpdateTowerInfo)
                    mTowerSelector.updateTowerInfo();
                return true;
            }
        }
        return false;
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

        doEnhanceTower(mTowerSelector.getSelectedTower(), true);
    }

    public void enhanceTowerMax() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    enhanceTowerMax();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        if (selectedTower != null && selectedTower.isEnhanceable()) {
            while ((selectedTower.getEnhanceCost() <= mScoreBoard.getCredits()) && selectedTower.isEnhanceable()) {
                mScoreBoard.takeCredits(selectedTower.getEnhanceCost());
                selectedTower.enhance();
                mTowerSelector.updateTowerInfo();
            }
        }
    }

    public void relayEnhanceTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    relayEnhanceTower();
                }
            });
            return;
        }

        Tower selectedTower = mTowerSelector.getSelectedTower();
        //if (!doEnhanceTower(selectedTower, true)) return;
        doEnhanceTower(selectedTower, true);

        StreamIterator<Tower> allOthers = mGameEngine
                .getEntitiesByType(EntityTypes.TOWER)
                .filter(selectedTower.getClass())
                .filter(Tower.isNotThis(selectedTower))
                .filter(Tower.isThisLevel(selectedTower.getLevel() - 1))
                .cast(Tower.class);

        while (allOthers.hasNext())
            doEnhanceTower(allOthers.next(), false);
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

    public void relayTowerStrategy() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    relayTowerStrategy();
                }
            });
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
        TowerStrategy selectedStrategy = selectedTowerAimer.getStrategy();

        StreamIterator<Tower> allOthers = mGameEngine
                .getEntitiesByType(EntityTypes.TOWER)
                .filter(selectedTower.getClass())
                .filter(Tower.isNotThis(selectedTower))
                .cast(Tower.class);

        while (allOthers.hasNext()) {
            Aimer otherAimer = allOthers.next().getAimer();
            if (otherAimer != null) {
                otherAimer.setStrategy(selectedStrategy);
            }
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

    public void relayLockTarget() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    relayLockTarget();
                }
            });
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
        boolean selectedLock = selectedTowerAimer.doesLockTarget();

        StreamIterator<Tower> allOthers = mGameEngine
                .getEntitiesByType(EntityTypes.TOWER)
                .filter(selectedTower.getClass())
                .filter(Tower.isNotThis(selectedTower))
                .cast(Tower.class);

        while (allOthers.hasNext()) {
            Aimer otherAimer = allOthers.next().getAimer();
            if (otherAimer != null) {
                otherAimer.setLockTarget(selectedLock);
            }
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
