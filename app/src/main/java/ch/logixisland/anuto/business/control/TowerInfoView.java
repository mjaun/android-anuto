package ch.logixisland.anuto.business.control;

import ch.logixisland.anuto.entity.tower.Tower;

public interface TowerInfoView {
    void showTowerInfo(Tower tower);
    void hideTowerInfo();
}
