package ch.logixisland.anuto.business.game;

import android.content.Context;
import android.content.SharedPreferences;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;

public class HighScores {

    private final SharedPreferences mHighScores;
    private final ScoreBoard mScoreBoard;
    private final GameEngine mGameEngine;

    public HighScores(Context context, GameEngine gameEngine, ScoreBoard scoreBoard) {
        mHighScores = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE);
        mScoreBoard = scoreBoard;
        mGameEngine = gameEngine;
    }

    public int getHighScore(String mapId) {
        return mHighScores.getInt(mapId, 0);
    }

    public void updateHighScore() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    updateHighScore();
                }
            });
            return;
        }

        String mapId = mGameEngine.getGameConfiguration().getGameMap().getId();
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
