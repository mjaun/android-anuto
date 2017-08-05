package ch.logixisland.anuto.business.level;

import java.util.Iterator;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;

public class TowerAging {

    private final GameEngine mGameEngine;
    private final LevelLoader mLevelLoader;

    public TowerAging(GameEngine gameEngine, LevelLoader levelLoader) {
        mGameEngine = gameEngine;
        mLevelLoader = levelLoader;
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

    public void ageTower(final Tower tower) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    ageTower(tower);
                }
            });
            return;
        }

        int value = tower.getValue();
        value = Math.round(value * mLevelLoader.getGameSettings().getAgeModifier());
        tower.setValue(value);
    }
}
