package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.content.SharedPreferences;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class HighScores {

    private final SharedPreferences mHighScores;
    private final ScoreBoard mScoreBoard;
    private final GameEngine mGameEngine;
    private final GameLoader mGameLoader;

    public HighScores(Context context, GameEngine gameEngine, ScoreBoard scoreBoard, GameLoader gameLoader) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
        mScoreBoard = scoreBoard;
        mGameEngine = gameEngine;
        mGameLoader = gameLoader;
    }

    public int getHighScore(String mapId) {
        return mHighScores.getInt(mapId, 0);
    }

    public void updateHighScore() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(this::updateHighScore);
            return;
        }

        String mapId = mGameLoader.getCurrentMapId();
        int highScore = getHighScore(mapId);
        int score = mScoreBoard.getScore();

        if (score > highScore) {
            mHighScores.edit().putInt(mapId, score).apply();
        }
    }

    public void clearHighScores() {
        mHighScores.edit()
                .clear()
                .apply();
    }

}
