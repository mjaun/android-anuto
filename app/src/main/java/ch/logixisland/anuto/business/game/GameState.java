package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.score.LivesListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;

public class GameState implements ThemeListener, LivesListener {

    private final static String TAG = GameState.class.getSimpleName();

    private final GameEngine mGameEngine;
    private final ScoreBoard mScoreBoard;

    private boolean mGameOver = false;
    private boolean mGameStarted = false;

    private List<GameStateListener> mListeners = new CopyOnWriteArrayList<>();

    public GameState(GameEngine gameEngine, ThemeManager themeManager, ScoreBoard scoreBoard) {
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

        for (GameStateListener listener : mListeners) {
            listener.gameRestart();
        }

        mGameOver = false;
        mGameStarted = false;
    }

    public boolean isGameOver() {
        return mGameOver;
    }

    public boolean isGameStarted() {
        return !mGameOver && mGameStarted;
    }

    public void addListener(GameStateListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameStateListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void livesChanged(int lives) {
        if (!mGameOver && mScoreBoard.getLives() < 0) {
            mGameOver = true;

            for (GameStateListener listener : mListeners) {
                listener.gameOver();
            }
        }
    }

    @Override
    public void themeChanged(Theme theme) {
        restart();
    }

    public void setGameStarted() {
        mGameStarted = true;
    }

}
