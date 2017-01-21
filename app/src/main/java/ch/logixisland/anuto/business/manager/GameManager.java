package ch.logixisland.anuto.business.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameManager {

    private final static String TAG = GameManager.class.getSimpleName();

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final LevelLoader mLevelLoader;
    private final WaveManager mWaveManager;

    private volatile boolean mGameOver;

    private List<GameListener> mListeners = new CopyOnWriteArrayList<>();

    private final LivesListener mLivesListener = new LivesListener() {
        @Override
        public void livesChanged(int lives) {
            if (!mGameOver && mScoreBoard.getLives() < 0) {
                mGameOver = true;

                for (GameListener listener : mListeners) {
                    listener.gameOver();
                }
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

        for (GameListener listener : mListeners) {
            listener.gameStarted();
        }
    }


    public boolean isGameOver() {
        return mGameOver;
    }


    public void addListener(GameListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        mListeners.remove(listener);
    }

}
