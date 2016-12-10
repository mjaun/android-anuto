package ch.logixisland.anuto.game.business.control;

import ch.logixisland.anuto.game.business.score.ScoreBoard;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.plateau.Plateau;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TowerInserter {

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;

    private Tower mInsertedTower;

    public TowerInserter(GameEngine gameEngine, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;
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

        if (mInsertedTower != null) {
            mInsertedTower = tower;
            mGameEngine.add(tower);
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
            mScoreBoard.takeCredits(mInsertedTower.getValue());
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
            mInsertedTower = null;
        }
    }

}
