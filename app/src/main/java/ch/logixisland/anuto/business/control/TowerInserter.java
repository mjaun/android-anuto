package ch.logixisland.anuto.business.control;

import java.util.Iterator;

import ch.logixisland.anuto.business.level.TowerAging;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerFactory;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TowerInserter {

    private final GameEngine mGameEngine;
    private final GameManager mGameManager;
    private final TowerFactory mTowerFactory;
    private final TowerSelector mTowerSelector;
    private final TowerAging mTowerAging;
    private final ScoreBoard mScoreBoard;

    private Tower mInsertedTower;
    private Plateau mCurrentPlateau;

    public TowerInserter(GameEngine gameEngine, GameManager gameManager, TowerFactory towerFactory,
                         TowerSelector towerSelector, TowerAging towerAging, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mGameManager = gameManager;
        mTowerFactory = towerFactory;
        mTowerSelector = towerSelector;
        mTowerAging = towerAging;
        mScoreBoard = scoreBoard;
    }

    public void insertTower(final String towerName) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    insertTower(towerName);
                }
            });
            return;
        }

        if (mInsertedTower == null && !mGameManager.isGameOver()) {
            showTowerLevels();
            mInsertedTower = mTowerFactory.createTower(towerName);
        }
    }

    public void setPosition(final Vector2 position) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    setPosition(position);
                }
            });
            return;
        }

        if (mInsertedTower != null) {
            Plateau closestPlateau = mGameEngine.get(Types.PLATEAU)
                    .cast(Plateau.class)
                    .filter(Plateau.unoccupied())
                    .min(Entity.distanceTo(position));

            if (closestPlateau != null) {
                if (mCurrentPlateau == null) {
                    mGameEngine.add(mInsertedTower);
                    mTowerSelector.selectTower(mInsertedTower);
                }

                mCurrentPlateau = closestPlateau;
                mInsertedTower.setPosition(mCurrentPlateau.getPosition());
            } else {
                cancel();
            }
        }
    }

    public void buyTower() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    buyTower();
                }
            });
            return;
        }

        if (mInsertedTower != null && mCurrentPlateau != null) {
            mInsertedTower.setEnabled(true);
            mCurrentPlateau.setOccupant(mInsertedTower);

            mScoreBoard.takeCredits(mInsertedTower.getValue());
            mTowerAging.ageTower(mInsertedTower);

            mTowerSelector.selectTower(null);
            hideTowerLevels();

            mCurrentPlateau = null;
            mInsertedTower = null;
        }
    }

    public void cancel() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    cancel();
                }
            });
            return;
        }

        if (mInsertedTower != null) {
            mGameEngine.remove(mInsertedTower);

            hideTowerLevels();
            mCurrentPlateau = null;
            mInsertedTower = null;
        }
    }

    private void showTowerLevels() {
        Iterator<Tower> towers = mGameEngine.get(Types.TOWER).cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            tower.showLevel();
        }
    }

    private void hideTowerLevels() {
        Iterator<Tower> towers = mGameEngine.get(Types.TOWER).cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            tower.hideLevel();
        }
    }

}
