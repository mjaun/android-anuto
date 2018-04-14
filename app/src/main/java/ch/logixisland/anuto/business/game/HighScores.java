package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.content.SharedPreferences;

import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.logic.GameEngine;

public class HighScores implements GameStateListener {

    private final SharedPreferences mHighScores;
    private final ScoreBoard mScoreBoard;
    private final GameEngine mGameEngine;

    public HighScores(Context context, GameEngine gameEngine, GameState gameState, ScoreBoard scoreBoard) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
        mScoreBoard = scoreBoard;
        mGameEngine = gameEngine;
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
        String mapId = mGameEngine.getGameConfiguration().getMapId();
        updateHighScore(mapId, mScoreBoard.getScore());
    }

    private void updateHighScore(String mapId, int highScore) {
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
