package ch.logixisland.anuto.business.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.level.GameSpeedManager;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.score.HighScoreBoard;
import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;

public class GameManager {

    private final static String TAG = GameManager.class.getSimpleName();

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final HighScoreBoard mHighScoreBoard;
    private final LevelLoader mLevelLoader;
    private final WaveManager mWaveManager;
    private final GameSpeedManager mSpeedManager;
    private final ThemeManager mThemeManager;

    private volatile boolean mGameOver = false;

    private List<GameListener> mListeners = new CopyOnWriteArrayList<>();

    private final LivesListener mLivesListener = new LivesListener() {
        @Override
        public void livesChanged(int lives) {
            if (!mGameOver && mScoreBoard.getLives() < 0) {
                mGameOver = true;

                for (GameListener listener : mListeners) {
                    listener.gameOver();
                }

                String levelId = mLevelLoader.getLevelInfo().getLevelId();
                mHighScoreBoard.setHighScore(levelId, mScoreBoard.getScore());
            }
        }
    };

    private final ThemeListener mThemeListener = new ThemeListener() {
        @Override
        public void themeChanged() {
            restart();
        }
    };

    public GameManager(GameEngine gameEngine, ScoreBoard scoreBoard, HighScoreBoard highScoreBoard,
                       LevelLoader levelLoader, WaveManager waveManager, GameSpeedManager speedManager,
                       ThemeManager themeManager) {
        mGameEngine = gameEngine;
        mLevelLoader = levelLoader;
        mScoreBoard = scoreBoard;
        mHighScoreBoard = highScoreBoard;
        mWaveManager = waveManager;
        mSpeedManager = speedManager;
        mThemeManager = themeManager;

        mScoreBoard.addLivesListener(mLivesListener);
        mThemeManager.addListener(mThemeListener);
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
        mSpeedManager.reset();

        mGameOver = false;

        for (GameListener listener : mListeners) {
            listener.gameStarted();
        }
    }


    public boolean isGameOver() {
        return mGameOver;
    }

    public boolean isGameStarted() {
        return !mGameOver && mWaveManager.getWaveNumber() > 0;
    }


    public void addListener(GameListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        mListeners.remove(listener);
    }

}
