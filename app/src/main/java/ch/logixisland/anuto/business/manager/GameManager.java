package ch.logixisland.anuto.business.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;

public class GameManager implements ThemeListener, LivesListener {

    private final static String TAG = GameManager.class.getSimpleName();

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;

    private volatile boolean mGameOver = false;

    private List<GameListener> mListeners = new CopyOnWriteArrayList<>();

    public GameManager(GameEngine gameEngine, ThemeManager themeManager, ScoreBoard scoreBoard) {
        mGameEngine = gameEngine;
        mScoreBoard = scoreBoard;

        mScoreBoard.addLivesListener(this);
        themeManager.addListener(this);
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

        for (GameListener listener : mListeners) {
            listener.gameRestart();
        }

        mGameOver = false;
    }

    public boolean isGameOver() {
        return mGameOver;
    }

    public boolean isGameStarted() {
        return !mGameOver && mScoreBoard.getScore() > 0;
    }

    public void addListener(GameListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void livesChanged(int lives) {
        if (!mGameOver && mScoreBoard.getLives() < 0) {
            mGameOver = true;

            for (GameListener listener : mListeners) {
                listener.gameOver();
            }
        }
    }

    @Override
    public void themeChanged(Theme theme) {
        restart();
    }

}
