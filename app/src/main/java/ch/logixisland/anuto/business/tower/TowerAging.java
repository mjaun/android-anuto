package ch.logixisland.anuto.business.tower;

import java.util.Iterator;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;

public class TowerAging {

    private final GameEngine mGameEngine;

    public TowerAging(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void ageTowers() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    ageTowers();
                }
            });
            return;
        }

        Iterator<Tower> towers = mGameEngine
                .getEntitiesByType(Types.TOWER)
                .cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            ageTower(tower);
        }
    }

    public void ageTower(final Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    ageTower(tower);
                }
            });
            return;
        }

        int value = tower.getValue();
        value = Math.round(value * mGameEngine.getGameConfiguration().getTowerSettingsRoot().getAgeModifier());
        tower.setValue(value);
    }
}
