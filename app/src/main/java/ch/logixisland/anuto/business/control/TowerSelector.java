package ch.logixisland.anuto.business.control;

import java.lang.ref.WeakReference;

import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.EntityListener;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerListener;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TowerSelector {

    private final GameEngine mGameEngine;
    private final GameManager mGameManager;
    private final ScoreBoard mScoreBoard;

    private WeakReference<TowerInfoView> mTowerInfoView;
    private TowerInfo mTowerInfo;
    private Tower mSelectedTower;

    private final EntityListener mEntityListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity obj) {
            selectTower(null);
        }
    };

    private final TowerListener mTowerListener = new TowerListener() {
        @Override
        public void damageInflicted(float totalDamage) {
            updateTowerInfo();
        }

        @Override
        public void valueChanged(int value) {
            updateTowerInfo();
        }
    };

    private final CreditsListener mCreditsListener = new CreditsListener() {
        @Override
        public void creditsChanged(int credits) {
            if (mTowerInfo != null) {
                updateTowerInfo();
            }
        }
    };

    private final GameListener mGameListener = new GameListener() {
        @Override
        public void gameStarted() {
            if (mTowerInfo != null) {
                updateTowerInfo();
            }
        }

        @Override
        public void gameOver() {
            if (mTowerInfo != null) {
                updateTowerInfo();
            }
        }
    };

    public TowerSelector(GameEngine gameEngine, GameManager gameManager, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mGameManager = gameManager;
        mScoreBoard = scoreBoard;

        mScoreBoard.addCreditsListener(mCreditsListener);
        mGameManager.addListener(mGameListener);
    }

    public void setTowerInfoView(TowerInfoView view) {
        mTowerInfoView = new WeakReference<>(view);
    }

    public TowerInfo getTowerInfo() {
        return mTowerInfo;
    }

    public void selectTowerAt(Vector2 position) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final Vector2 finalPosition = position;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    selectTowerAt(finalPosition);
                }
            });
            return;
        }

        Tower closest = (Tower) mGameEngine
                .get(Types.TOWER)
                .min(Entity.distanceTo(position));

        if (closest != null && closest.getDistanceTo(position) < 0.6f) {
            selectTower(closest);
        } else {
            selectTower(null);
        }
    }

    public void selectTower(Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final Tower finalTower = tower;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    selectTower(finalTower);
                }
            });
            return;
        }

        hideTowerInfo();

        if (tower != null) {
            if (mSelectedTower == tower) {
                showTowerInfo();
            } else {
                setSelectedTower(tower);
            }
        } else {
            setSelectedTower(null);
        }
    }

    public void showTowerInfo(Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    showTowerInfo();
                }
            });
            return;
        }

        setSelectedTower(tower);
        showTowerInfo();
    }

    public void updateTowerInfo() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    updateTowerInfo();
                }
            });
            return;
        }

        if (mTowerInfo != null) {
            showTowerInfo();
        }
    }

    Tower getSelectedTower() {
        return mSelectedTower;
    }

    private void setSelectedTower(Tower tower) {
        if (mSelectedTower != null) {
            mSelectedTower.removeListener(mEntityListener);
            mSelectedTower.removeListener(mTowerListener);
            mSelectedTower.hideRange();
        }

        mSelectedTower = tower;

        if (mSelectedTower != null) {
            mSelectedTower.addListener(mEntityListener);
            mSelectedTower.addListener(mTowerListener);
            mSelectedTower.showRange();
        }
    }

    private void showTowerInfo() {
        mTowerInfo = new TowerInfo(
                mSelectedTower,
                mScoreBoard.getCredits(),
                mGameManager.isGameOver()
        );

        TowerInfoView view = mTowerInfoView.get();

        if (view != null) {
            view.showTowerInfo(mTowerInfo);
        }
    }

    private void hideTowerInfo() {
        mTowerInfo = null;

        TowerInfoView view = mTowerInfoView.get();

        if (view != null) {
            view.hideTowerInfo();
        }
    }

}
