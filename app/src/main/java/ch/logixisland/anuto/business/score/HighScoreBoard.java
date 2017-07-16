package ch.logixisland.anuto.business.score;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreBoard {

    private final SharedPreferences mHighScores;

    public HighScoreBoard(Context context) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
    }

    public int getHighScore(String levelId) {
        return mHighScores.getInt(levelId, 0);
    }

    public void setHighScore(String levelId, int highScore) {
        int currentHighScore = getHighScore(levelId);

        if (highScore > currentHighScore) {
            resetHighScore(levelId, highScore);
        }
    }

    public void clearHighScores() {
        mHighScores.edit()
                .clear()
                .apply();
    }

    private void resetHighScore(String levelId, int highScore) {
        mHighScores.edit()
                .putInt(levelId, highScore)
                .apply();
    }

}
