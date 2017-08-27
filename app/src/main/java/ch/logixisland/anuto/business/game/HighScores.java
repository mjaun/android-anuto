package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.content.SharedPreferences;

import ch.logixisland.anuto.business.score.ScoreBoard;

public class HighScores implements GameStateListener {

    private final SharedPreferences mHighScores;
    private final ScoreBoard mScoreBoard;
    private final GameConfigurationLoader mGameConfigurationLoader;

    public HighScores(Context context, GameState gameState, ScoreBoard scoreBoard, GameConfigurationLoader gameConfigurationLoader) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
        mScoreBoard = scoreBoard;
        mGameConfigurationLoader = gameConfigurationLoader;
        gameState.addListener(this);
    }

    public int getHighScore(String mapId) {
        return mHighScores.getInt(mapId, 0);
    }

    @Override
    public void gameRestart() {

    }

    @Override
    public void gameOver() {
        String mapId = mGameConfigurationLoader.getMapInfo().getMapId();
        int score = mScoreBoard.getScore();
        setHighScore(mapId, score);
    }

    private void setHighScore(String mapId, int highScore) {
        int currentHighScore = getHighScore(mapId);

        if (highScore > currentHighScore) {
            mHighScores.edit()
                    .putInt(mapId, highScore)
                    .apply();
        }
    }

    public void clearHighScores() {
        mHighScores.edit()
                .clear()
                .apply();
    }

}
