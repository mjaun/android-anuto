package ch.logixisland.anuto;

import android.util.Log;

import java.util.concurrent.CountDownLatch;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.SaveGameInfo;

public abstract class GameSimulator {

    private final static String TAG = GameSimulator.class.getSimpleName();

    private final GameFactory mGameFactory;

    private CountDownLatch mFinishedLatch;

    private SaveGameInfo mSaveGameInfo = null;

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
            deleteSaveGame();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void tick();

    protected GameFactory getGameFactory() {
        return mGameFactory;
    }

    protected void saveGame() {
        Log.i(TAG, "Saving game...");
        deleteSaveGame();
        mSaveGameInfo = mGameFactory.getGameSaver().saveGame();
    }

    protected void loadGame() {
        if (mSaveGameInfo != null) {
            Log.i(TAG, "Loading game...");
            mGameFactory.getGameLoader().loadGame(mGameFactory.getSaveGameRepository().getGameStateFile(mSaveGameInfo));
            installTickHandler();
        }
    }

    protected void deleteSaveGame() {
        if (mSaveGameInfo != null) {
            Log.i(TAG, "Deleting save game...");
            mGameFactory.getSaveGameRepository().deleteSaveGame(mSaveGameInfo);
            mSaveGameInfo = null;
        }
    }

    protected void autoSaveAndLoad() {
        Log.i(TAG, "Testing auto save and load...");
        mGameFactory.getGameSaver().autoSaveGame();
        mGameFactory.getGameLoader().autoLoadGame();
        installTickHandler();
    }

    private void loadDefaultMap() {
        loadMap(mGameFactory.getMapRepository().getDefaultMapId());
    }

    private void loadMap(String mapId) {
        mGameFactory.getGameLoader().loadMap(mapId);
        waitForGameRestarted();
        adjustSettings();
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

    private void adjustSettings() {
        // make it a little bit easier for the simulator to be sure that higher tiers are reached
        mGameFactory.getScoreBoard().giveCredits(500000, false);
    }

    private void installTickHandler() {
        mGameFactory.getGameEngine().add(() -> {
            if (mGameFactory.getGameState().isGameOver()) {
                simulationFinished();
            } else {
                GameSimulator.this.tick();
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
