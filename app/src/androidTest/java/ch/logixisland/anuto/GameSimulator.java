package ch.logixisland.anuto;

import android.util.Log;

import java.util.concurrent.CountDownLatch;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.engine.logic.loop.TickListener;

public abstract class GameSimulator {

    private final static String TAG = GameSimulator.class.getSimpleName();

    private final GameFactory mGameFactory;

    private CountDownLatch mFinishedLatch;

    GameSimulator(GameFactory gameFactory) {
        mGameFactory = gameFactory;
    }

    void startSimulation() {
        mFinishedLatch = new CountDownLatch(1);
        mGameFactory.getGameEngine().setTicksPerLoop(20);
        loadDefaultMap();
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

    protected void saveAndLoad() {
        mGameFactory.getGameLoader().saveGame();
        mGameFactory.getGameLoader().loadGame();
        installTickHandler();
    }

    private void loadDefaultMap() {
        loadMap(mGameFactory.getMapRepository().getDefaultMapId());
    }

    private void loadMap(String mapId) {
        mGameFactory.getGameLoader().loadMap(mapId);
        waitForGameRestarted();
        installTickHandler();
    }

    private void waitForGameRestarted() {
        final GameState gameState = mGameFactory.getGameState();
        final CountDownLatch loadMapLatch = new CountDownLatch(1);

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

    private void installTickHandler() {
        mGameFactory.getGameEngine().add(new TickListener() {

            @Override
            public void tick() {
                if (mGameFactory.getGameState().isGameOver()) {
                    simulationFinished();
                } else {
                    GameSimulator.this.tick();
                }
            }
        });
    }

    private void simulationFinished() {
        if (mFinishedLatch.getCount() > 0) {
            Log.i(TAG, String.format("final wave=%d", mGameFactory.getWaveManager().getWaveNumber()));
            Log.i(TAG, String.format("final score=%d", mGameFactory.getScoreBoard().getScore()));
            mFinishedLatch.countDown();
        }
    }

}
