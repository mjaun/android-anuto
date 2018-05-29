package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.engine.logic.persistence.Persister;

public class GameState implements ScoreBoard.LivesListener, Persister {

    public interface Listener {
        void gameRestart();
        void gameOver();
    }

    private final ScoreBoard mScoreBoard;
    private final HighScores mHighScores;
    private final TowerSelector mTowerSelector;

    private boolean mGameOver = false;
    private boolean mGameStarted = false;

    private List<Listener> mListeners = new CopyOnWriteArrayList<>();

    public GameState(ScoreBoard scoreBoard, HighScores highScores, TowerSelector towerSelector) {
        mScoreBoard = scoreBoard;
        mHighScores = highScores;
        mTowerSelector = towerSelector;

        mScoreBoard.addLivesListener(this);
    }

    public boolean isGameOver() {
        return mGameOver;
    }

    public boolean isGameStarted() {
        return !mGameOver && mGameStarted;
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    public void gameStarted() {
        mGameStarted = true;
    }

    @Override
    public void livesChanged(int lives) {
        if (!mGameOver && mScoreBoard.getLives() < 0) {
            setGameOver(true);
        }
    }

    @Override
    public void writeDescriptor(ch.logixisland.anuto.data.state.GameState gameState) {

    }

    @Override
    public void readDescriptor(ch.logixisland.anuto.data.state.GameState gameState) {
        setGameOver(gameState.getLives() < 0);
        mGameStarted = gameState.getWaveNumber() > 0;
    }

    private void setGameOver(boolean gameOver) {
        mGameOver = gameOver;

        if (gameOver) {
            mHighScores.updateHighScore();
            mTowerSelector.setControlsEnabled(false);

            for (Listener listener : mListeners) {
                listener.gameOver();
            }
        }

        if (!gameOver) {
            mGameStarted = false;
            mTowerSelector.setControlsEnabled(true);

            for (Listener listener : mListeners) {
                listener.gameRestart();
            }
        }
    }
}
