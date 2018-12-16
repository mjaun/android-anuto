package ch.logixisland.anuto;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.tower.TowerInserter;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.iterator.Function;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public abstract class GameSimulator {

    private final static String TAG = GameSimulator.class.getSimpleName();

    private final GameFactory mGameFactory;
    private final CountDownLatch mFinishedLatch;
    private final HashMap<String, Integer> mTowerTierCache = new HashMap<>();

    GameSimulator() {
        mGameFactory = AnutoApplication.getInstance().getGameFactory();
        mGameFactory.getGameEngine().setTicksPerLoop(10);

        loadDefaultMap();

        mFinishedLatch = new CountDownLatch(1);

        mGameFactory.getGameEngine().add(new TickListener() {
            private final TickTimer mSimulationTickTimer = TickTimer.createInterval(0.2f);

            @Override
            public void tick() {
                if (mGameFactory.getGameState().isGameOver()) {
                    simulationFinished();
                } else if (mSimulationTickTimer.tick()) {
                    GameSimulator.this.tick();
                }
            }
        });
    }

    void waitForFinished() {
        try {
            mFinishedLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void tick();

    protected GameFactory getGameFactory() {
        return mGameFactory;
    }

    protected StreamIterator<Plateau> getFreePlateaus() {
        final GameEngine gameEngine = getGameFactory().getGameEngine();

        return gameEngine.getEntitiesByType(Types.PLATEAU)
                .cast(Plateau.class)
                .filter(Plateau.unoccupied());
    }

    protected StreamIterator<Tower> getBuildableTowers() {
        final TowerInserter towerInserter = getGameFactory().getTowerInserter();

        return StreamIterator.fromIterable(towerInserter.getAssignedSlots())
                .map(new Function<Integer, Tower>() {
                    @Override
                    public Tower apply(Integer slot) {
                        return towerInserter.createPreviewTower(slot);
                    }
                });
    }

    protected StreamIterator<Tower> getTowers() {
        final GameEngine gameEngine = getGameFactory().getGameEngine();

        return gameEngine.getEntitiesByType(Types.TOWER)
                .cast(Tower.class);
    }

    protected int getTowerTier(Tower tower) {
        if (mTowerTierCache.isEmpty()) {
            initTowerTierCache();
        }

        return mTowerTierCache.get(tower.getEntityName());
    }

    private void simulationFinished() {
        if (mFinishedLatch.getCount() > 0) {
            Log.i(TAG, String.format("final wave=%d", mGameFactory.getWaveManager().getWaveNumber()));
            Log.i(TAG, String.format("final score=%d", mGameFactory.getScoreBoard().getScore()));
            mFinishedLatch.countDown();
        }
    }

    private void loadDefaultMap() {
        loadMap(mGameFactory.getMapRepository().getDefaultMapId());
    }

    private void loadMap(String mapId) {
        final GameState gameState = mGameFactory.getGameState();
        final CountDownLatch loadMapLatch = new CountDownLatch(1);

        mGameFactory.getGameLoader().loadMap(mapId);

        gameState.addListener(new GameState.Listener() {
            @Override
            public void gameRestart() {
                loadMapLatch.countDown();
                gameState.removeListener(this);
            }

            @Override
            public void gameOver() {

            }
        });

        try {
            loadMapLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initTowerTierCache() {
        EntityRegistry entityRegistry = mGameFactory.getEntityRegistry();
        StreamIterator<Tower> iterator = getBuildableTowers();
        mTowerTierCache.clear();

        while (iterator.hasNext()) {
            int tier = 1;
            Tower tower = iterator.next();
            mTowerTierCache.put(tower.getEntityName(), tier);

            while (tower.isUpgradeable()) {
                tower = (Tower) entityRegistry.createEntity(tower.getUpgradeName());
                tier += 1;
                mTowerTierCache.put(tower.getEntityName(), tier);
            }
        }
    }

}
