package ch.logixisland.anuto;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import ch.logixisland.anuto.business.game.GameState;
import ch.logixisland.anuto.business.game.SaveGameInfo;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.view.game.GameActivity;

public abstract class GameSimulator {

    private final static String TAG = GameSimulator.class.getSimpleName();

    private final GameActivity mGameActivity;
    private final GameFactory mGameFactory;

    private CountDownLatch mFinishedLatch;

    private SaveGameInfo mLastSG = null;

    GameSimulator(GameActivity gameActivity, GameFactory gameFactory) {
        mGameActivity = gameActivity;
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
            deleteSG();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void tick();

    protected GameFactory getGameFactory() {
        return mGameFactory;
    }

    protected void saveSG() {
        deleteSG();
        mLastSG = SaveGameInfo.createSGI(mGameFactory.getGameLoader(), mGameFactory.getGameSaver().makeNewSavegame());
        Thread thread = new Thread() {
            public void run() {
                mGameActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mGameActivity, mGameActivity.getString(ch.logixisland.anuto.R.string.saveGameSuccessful), Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        thread.start();
    }


    private static Predicate<SaveGameInfo> byFolder(final File folder) {
        return new Predicate<SaveGameInfo>() {
            @Override
            public boolean apply(SaveGameInfo value) {
                return value.getFolder().equals(folder);
            }
        };
    }

    protected void loadSG() {
        if (mLastSG != null) {
            if (!StreamIterator.fromIterable(mGameFactory.getSaveGameRepository()
                    .getSavegameInfos()).filter(byFolder(mLastSG.getFolder())).isEmpty())
                mGameFactory.getGameLoader().loadGameState(mLastSG.getSavegameState());
            else
                mLastSG = null;
            installTickHandler();
        }
    }

    protected void deleteSG() {
        if (mLastSG != null) {
            mGameFactory.getSaveGameRepository().deleteSavegame(mLastSG.getFolder());
            mLastSG = null;
        }
    }

    protected void saveAndLoad() {
        mGameFactory.getGameSaver().autoSaveGame();
        mGameFactory.getGameLoader().loadGame();
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
        mGameFactory.getScoreBoard().giveCredits(200000, false);
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
