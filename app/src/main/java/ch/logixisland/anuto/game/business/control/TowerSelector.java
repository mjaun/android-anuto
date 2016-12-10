package ch.logixisland.anuto.game.business.control;

import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class TowerSelector {

    private final GameEngine mGameEngine;

    private TowerInfoView mTowerInfoView;
    private Tower mSelectedTower;
    private TowerInfo mVisibleTowerInfo;

    public TowerSelector(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void setTowerInfoView(TowerInfoView view) {
        mTowerInfoView = view;
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

        hideTowerInfo();

        if (closest != null && closest.getDistanceTo(position) < 0.6f) {
            if (mSelectedTower == closest) {
                showTowerInfo(closest);
            } else {
                setSelectedTower(closest);
            }
        } else {
            setSelectedTower(null);
        }
    }

    Tower getSelectedTower() {
        return mSelectedTower;
    }

    private void updateTowerInfo() {
        if (mTowerInfoView != null && mVisibleTowerInfo != null) {
            mVisibleTowerInfo = new TowerInfo(mSelectedTower);
            mTowerInfoView.updateTowerInfo(mVisibleTowerInfo);
        }
    }

    private void hideTowerInfo() {
        if (mVisibleTowerInfo != null) {
            if (mTowerInfoView != null) {
                mTowerInfoView.hideTowerInfo();
            }
            mVisibleTowerInfo = null;
        }
    }

    private void showTowerInfo(Tower tower) {
        mVisibleTowerInfo = new TowerInfo(tower);

        if (mTowerInfoView != null) {
            mTowerInfoView.showTowerInfo(mVisibleTowerInfo);
        }
    }

    private void setSelectedTower(Tower tower) {
        if (mSelectedTower != null) {
            mSelectedTower.hideRange();
        }

        mSelectedTower = tower;

        if (mSelectedTower != null) {
            mSelectedTower.showRange();
        }
    }

}
