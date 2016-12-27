package ch.logixisland.anuto.game.business.control;

import ch.logixisland.anuto.game.entity.tower.Tower;

public interface TowerInfoView {
    void showTowerInfo(Tower tower);
    void hideTowerInfo();
}
