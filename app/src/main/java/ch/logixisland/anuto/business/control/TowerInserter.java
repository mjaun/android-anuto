package ch.logixisland.anuto.business.control;

import java.util.Iterator;

import ch.logixisland.anuto.business.level.TowerAging;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TowerInserter {

    private final GameEngine mGameEngine;
    private final TowerSelector mTowerSelector;
    private final ScoreBoard mScoreBoard;
    private final TowerAging mTowerAging;

    private Tower mInsertedTower;
    private Plateau mCurrentPlateau;

    public TowerInserter(GameEngine gameEngine, ScoreBoard scoreBoard, TowerSelector towerSelector,
                         TowerAging towerAging) {
        mGameEngine = gameEngine;
        mTowerSelector = towerSelector;
        mScoreBoard = scoreBoard;
        mTowerAging = towerAging;
    }

    public void insertTower(Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final Tower finalTower = tower;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    insertTower(finalTower);
                }
            });
            return;
        }

        if (mInsertedTower == null) {
            showTowerLevels();
            mInsertedTower = tower;
            mGameEngine.add(tower);
            mTowerSelector.selectTower(mInsertedTower);
        }
    }

    public void setPosition(Vector2 position) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final Vector2 finalPosition = position;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    setPosition(finalPosition);
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

        if (mInsertedTower != null) {
            mInsertedTower.setEnabled(true);
            mInsertedTower.setPlateau(mCurrentPlateau);

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
