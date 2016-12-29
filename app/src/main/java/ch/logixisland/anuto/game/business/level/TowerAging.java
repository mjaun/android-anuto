package ch.logixisland.anuto.game.business.level;

import java.util.Iterator;

import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.tower.Tower;

public class TowerAging {

    private final GameEngine mGameEngine;

    private float mValueModifier;

    public TowerAging(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void setValueModifier(float valueModifier) {
        mValueModifier = valueModifier;
    }

    public void ageTowers() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    ageTowers();
                }
            });
            return;
        }

        Iterator<Tower> towers = mGameEngine
                .get(Types.TOWER)
                .cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            ageTower(tower);
        }
    }

    public void ageTower(Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final Tower finalTower = tower;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    ageTower(finalTower);
                }
            });
            return;
        }

        int value = tower.getValue();
        value = Math.round(value * mValueModifier);
        tower.setValue(value);
    }
}
