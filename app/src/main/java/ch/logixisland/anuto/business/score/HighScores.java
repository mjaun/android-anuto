package ch.logixisland.anuto.business.score;

import android.content.Context;
import android.content.SharedPreferences;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.manager.GameState;
import ch.logixisland.anuto.business.manager.GameStateListener;

public class HighScores implements GameStateListener {

    private final SharedPreferences mHighScores;
    private final ScoreBoard mScoreBoard;
    private final LevelLoader mLevelLoader;

    public HighScores(Context context, GameState gameState, ScoreBoard scoreBoard, LevelLoader levelLoader) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
        mScoreBoard = scoreBoard;
        mLevelLoader = levelLoader;
        gameState.addListener(this);
    }

    public int getHighScore(String levelId) {
        return mHighScores.getInt(levelId, 0);
    }

    @Override
    public void gameRestart() {

    }

    @Override
    public void gameOver() {
        String levelId = mLevelLoader.getLevelInfo().getLevelId();
        int score = mScoreBoard.getScore();
        setHighScore(levelId, score);
    }

    private void setHighScore(String levelId, int highScore) {
        int currentHighScore = getHighScore(levelId);

        if (highScore > currentHighScore) {
            mHighScores.edit()
                    .putInt(levelId, highScore)
                    .apply();
        }
    }

    public void clearHighScores() {
        mHighScores.edit()
                .clear()
                .apply();
    }

}
