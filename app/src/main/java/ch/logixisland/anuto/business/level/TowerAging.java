package ch.logixisland.anuto.business.level;

import java.util.Iterator;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.tower.Tower;

public class TowerAging {

    private final GameEngine mGameEngine;
    private final LevelLoader mLevelLoader;

    private final WaveListener mWaveListener = new WaveListener() {
        @Override
        public void nextWaveReady() {

        }

        @Override
        public void waveStarted() {

        }

        @Override
        public void waveFinished() {
            ageTowers();
        }
    };

    public TowerAging(GameEngine gameEngine, WaveManager waveManager, LevelLoader levelLoader) {
        mGameEngine = gameEngine;
        mLevelLoader = levelLoader;

        waveManager.addListener(mWaveListener);
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
        value = Math.round(value * mLevelLoader.getGameSettings().getAgeModifier());
        tower.setValue(value);
    }

    private void ageTowers() {
        Iterator<Tower> towers = mGameEngine
                .get(Types.TOWER)
                .cast(Tower.class);

        while (towers.hasNext()) {
            Tower tower = towers.next();
            ageTower(tower);
        }
    }
}
