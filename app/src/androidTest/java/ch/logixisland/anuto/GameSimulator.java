package ch.logixisland.anuto;

import android.util.Log;

import java.util.concurrent.CountDownLatch;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;

public abstract class GameSimulator {

    private final static String TAG = GameSimulator.class.getSimpleName();

    private final GameFactory mGameFactory;

    private CountDownLatch mFinishedLatch;

    GameSimulator(GameFactory gameFactory) {
        mGameFactory = gameFactory;
    }

    void startSimulation() {
        mGameFactory.getGameEngine().setTicksPerLoop(20);

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

}
