package ch.logixisland.anuto.game.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.logixisland.anuto.game.GameManager;

public class Score implements GameManager.OnGameOverListener {

    private static final String HighScoreKey = "ch.logixisland.anuto.HIGHSCORE";

    private SharedPreferences prefs;
    private String persistenceKey;

    private int mScore = 0;
    private int mPrevHighScore = 0;

    public void setLevel(String levelName) {

        persistenceKey = getPersistenceKey(levelName);

        if(prefs == null) {
            GameManager gm = GameManager.getInstance();
            prefs = PreferenceManager.getDefaultSharedPreferences(gm.getContext());
        }

        mPrevHighScore = prefs.getInt(persistenceKey,0);
    }

    public synchronized void modify(int adjustment){
        mScore += adjustment;
    }

    public int get(){
        return mScore;
    }

    public boolean isHighScore(){
        return mScore > mPrevHighScore;
    }

    public int getHighScore(){
        return Math.max(mScore, mPrevHighScore);
    }

    public void reset(){
        mScore = 0;
        mPrevHighScore = 0;
    }

    public void persist(){
        if( isHighScore() ) {
            prefs.edit().putInt(persistenceKey, mScore).apply();
        }
    }

    @Override
    public void onGameOver(){
        persist();
    }

    private String getPersistenceKey(String levelName) {
        return HighScoreKey + "." + levelName;
    }
}
