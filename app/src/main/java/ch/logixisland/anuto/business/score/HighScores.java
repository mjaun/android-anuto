package ch.logixisland.anuto.business.score;

import android.content.Context;
import android.content.SharedPreferences;

import ch.logixisland.anuto.business.level.LevelLoader;
import ch.logixisland.anuto.business.manager.GameListener;
import ch.logixisland.anuto.business.manager.GameManager;

public class HighScores implements GameListener {

    private final SharedPreferences mHighScores;
    private final ScoreBoard mScoreBoard;
    private final LevelLoader mLevelLoader;

    public HighScores(Context context, GameManager gameManager, ScoreBoard scoreBoard, LevelLoader levelLoader) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
        mScoreBoard = scoreBoard;
        mLevelLoader = levelLoader;
        gameManager.addListener(this);
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
