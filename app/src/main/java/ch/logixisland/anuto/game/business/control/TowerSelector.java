package ch.logixisland.anuto.game.business.control;

import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.EntityListener;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TowerSelector {

    private final GameEngine mGameEngine;

    private TowerInfoView mTowerInfoView;
    private Tower mSelectedTower;

    private final EntityListener mEntityListener = new EntityListener() {
        @Override
        public void entityRemoved(Entity obj) {
            selectTower(null);
        }
    };

    public TowerSelector(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void setTowerInfoView(TowerInfoView view) {
        mTowerInfoView = view;
    }

    Tower getSelectedTower() {
        return mSelectedTower;
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
            final Tower finalTower = tower;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    showTowerInfo(finalTower);
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

        showTowerInfo();
    }

    private void setSelectedTower(Tower tower) {
        if (mSelectedTower != null) {
            mSelectedTower.removeListener(mEntityListener);
            mSelectedTower.hideRange();
        }

        mSelectedTower = tower;

        if (mSelectedTower != null) {
            mSelectedTower.addListener(mEntityListener);
            mSelectedTower.showRange();
        }
    }

    private void hideTowerInfo() {
        if (mTowerInfoView != null) {
            mTowerInfoView.hideTowerInfo();
        }
    }

    private void showTowerInfo() {
        if (mTowerInfoView != null) {
            mTowerInfoView.showTowerInfo(mSelectedTower);
        }
    }

}
