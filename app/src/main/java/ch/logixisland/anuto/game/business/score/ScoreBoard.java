package ch.logixisland.anuto.game.business.score;

public class ScoreBoard {

    private boolean mGameOver;
    private boolean mGameWon;

    private int mCredits;
    private int mCreditsEarned;
    private int mScore;
    private int mLives;
    private int mWaveBonus;
    private int mEarlyBonus;

    public synchronized void reset() {
        mGameOver = false;
        mGameWon = false;

        mCredits = 0;
        mCreditsEarned = 0;
        mScore = 0;
        mLives = 0;
        mEarlyBonus = 0;
    }

    public synchronized void addScore(int score) {
        mScore += score;
    }

    public synchronized void giveLives(int lives) {
        mLives -= lives;
    }

    public synchronized void giveCredits(int credits, boolean earned) {
        mCredits += credits;

        if (earned) {
            mCreditsEarned += credits;

            if (!mGameOver) {
                mScore += credits;
            }
        }
    }

    public synchronized void takeCredits(int credits) {
        mCredits -= credits;
    }

    public synchronized void setWaveBonus(int waveBonus) {
        mWaveBonus = waveBonus;
    }

    public synchronized void setEarlyBonus(int earlyBonus) {
        mEarlyBonus = earlyBonus;
    }

    public int getCredits() {
        return mCredits;
    }

    public int getCreditsEarned() {
        return mCreditsEarned;
    }

    public int getScore() {
        return mScore;
    }

    public int getLives() {
        return mLives;
    }

    public int getWaveBonus() {
        return mWaveBonus;
    }

    public int getEarlyBonus() {
        return mEarlyBonus;
    }
}
