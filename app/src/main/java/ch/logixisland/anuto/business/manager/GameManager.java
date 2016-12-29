package ch.logixisland.anuto.business.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.level.TowerAging;
import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.level.WaveListener;
import ch.logixisland.anuto.business.level.WaveManager;
import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.util.data.LevelDescriptor;

public class GameManager {

    private final static String TAG = GameManager.class.getSimpleName();

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;
    private final LevelLoader mLevelLoader;
    private final WaveManager mWaveManager;
    private final TowerAging mTowerAging;

    private volatile boolean mGameOver;

    private List<GameListener> mListeners = new CopyOnWriteArrayList<>();

    private final WaveListener mWaveListener = new WaveListener() {
        @Override
        public void nextWaveReady() {

        }

        @Override
        public void waveStarted() {

        }

        @Override
        public void waveFinished() {
            mTowerAging.ageTowers();
        }
    };

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
                       TowerAging towerAging, WaveManager waveManager) {
        mGameEngine = gameEngine;
        mLevelLoader = levelLoader;
        mScoreBoard = scoreBoard;
        mWaveManager = waveManager;
        mTowerAging = towerAging;

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
        mTowerAging.setValueModifier(mLevelLoader.getLevel().getSettings().getAgeModifier());
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
