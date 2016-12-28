package ch.logixisland.anuto.game.business;

import android.util.Log;

import java.util.Iterator;

import ch.logixisland.anuto.game.business.level.LevelLoader;
import ch.logixisland.anuto.game.business.level.WaveListener;
import ch.logixisland.anuto.game.business.level.WaveManager;
import ch.logixisland.anuto.game.business.score.LivesListener;
import ch.logixisland.anuto.game.business.score.ScoreBoard;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.data.LevelDescriptor;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.util.container.ListenerList;

public class GameManager {

    private final static String TAG = GameManager.class.getSimpleName();

    public interface Listener {

    }

    public interface OnGameStartedListener extends Listener {
        void onGameStarted();
    }

    public interface OnGameOverListener extends Listener {
        void onGameOver();
    }

    public interface OnTowersAgedListener extends Listener {
        void onTowersAged();
    }

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final LevelLoader mLevelLoader;
    private final WaveManager mWaveManager;

    private volatile boolean mGameOver;

    private ListenerList<Listener> mListeners = new ListenerList<>();

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

    private final LivesListener mLivesListener = new LivesListener() {
        @Override
        public void livesChanged(int lives) {
            if (!mGameOver && mScoreBoard.getLives() < 0) {
                mGameOver = true;
                onGameOver();
            }
        }
    };

    public GameManager(GameEngine gameEngine, ScoreBoard scoreBoard, LevelLoader levelLoader,
                       WaveManager waveManager) {
        mGameEngine = gameEngine;
        mLevelLoader = levelLoader;
        mScoreBoard = scoreBoard;
        mWaveManager = waveManager;

        mGameOver = true;
        mScoreBoard.addLivesListener(mLivesListener);
        mWaveManager.addListener(mWaveListener);
    }

    public void setLevel(LevelDescriptor level) {
        if (mGameEngine.isThreadChangeNeeded()) {
            final LevelDescriptor finalLevel = level;
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    setLevel(finalLevel);
                }
            });
            return;
        }

        mLevelLoader.setLevel(level);
        restart();
    }

    public void restart() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    restart();
                }
            });
            return;
        }

        mLevelLoader.reset();
        mWaveManager.reset();

        mGameOver = false;
        onGameStarted();
    }


    public boolean isGameOver() {
        return mGameOver;
    }


    private void ageTowers() {
        Iterator<Tower> it = mGameEngine.get(Types.TOWER).cast(Tower.class);
        while (it.hasNext()) {
            Tower t = it.next();
            t.devalue(mLevelLoader.getLevel().getSettings().getAgeModifier());
        }

        onTowersAged();
    }


    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }


    private void onGameStarted() {
        Log.i(TAG, "Game started.");

        for (OnGameStartedListener l : mListeners.get(OnGameStartedListener.class)) {
            l.onGameStarted();
        }
    }

    private void onGameOver() {
        Log.i(TAG, "Game over.");

        for (OnGameOverListener l : mListeners.get(OnGameOverListener.class)) {
            l.onGameOver();
        }
    }

    private void onTowersAged() {
        for (OnTowersAgedListener l : mListeners.get(OnTowersAgedListener.class)) {
            l.onTowersAged();
        }
    }
}
