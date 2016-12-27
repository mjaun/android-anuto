package ch.logixisland.anuto.game.business.control;

import ch.logixisland.anuto.game.entity.tower.Tower;

public interface TowerSelectionListener {
    void selectedTowerChanged(Tower tower);
}
