package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.GameSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class ScoreBoard implements Persister {

    public interface Listener {
        void creditsChanged(int credits);

        void bonusChanged(int waveBonus, int earlyBonus);

        void livesChanged(int lives);
    }

    private final GameEngine mGameEngine;

    private int mCredits;
    private int mCreditsEarned;
    private int mLives;
    private int mEarlyBonus;
    private int mWaveBonus;

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    public ScoreBoard(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void takeLives(final int lives) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> takeLives(lives));
            return;
        }

        mLives -= lives;
        livesChanged();
    }

    public void giveCredits(final int credits, final boolean earned) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> giveCredits(credits, earned));
            return;
        }

        mCredits += credits;

        if (earned) {
            mCreditsEarned += credits;
        }

        creditsChanged();
    }

    public void takeCredits(final int credits) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> takeCredits(credits));
            return;
        }

        mCredits -= credits;
        creditsChanged();
    }

    public void setEarlyBonus(final int earlyBonus) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> setEarlyBonus(earlyBonus));
            return;
        }

        mEarlyBonus = earlyBonus;
        bonusChanged();
    }

    public void setWaveBonus(final int waveBonus) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> setWaveBonus(waveBonus));
            return;
        }

        mWaveBonus = waveBonus;
        bonusChanged();
    }

    public int getCredits() {
        return mCredits;
    }

    public int getCreditsEarned() {
        return mCreditsEarned;
    }

    public int getScore() {
        return mCreditsEarned;
    }

    public int getLives() {
        return mLives;
    }

    public int getEarlyBonus() {
        return mEarlyBonus;
    }

    public int getWaveBonus() {
        return mWaveBonus;
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void bonusChanged() {
        for (Listener listener : mListeners) {
            listener.bonusChanged(mWaveBonus, mEarlyBonus);
        }
    }

    private void creditsChanged() {
        for (Listener listener : mListeners) {
            listener.creditsChanged(mCredits);
        }
    }

    private void livesChanged() {
        for (Listener listener : mListeners) {
            listener.livesChanged(mLives);
        }
    }

    @Override
    public void resetState() {
        mLives = GameSettings.START_LIVES;
        mCredits = GameSettings.START_CREDITS;
        mCreditsEarned = 0;

        creditsChanged();
        livesChanged();
    }

    @Override
    public void writeState(KeyValueStore gameState) {
        gameState.putInt("lives", mLives);
        gameState.putInt("credits", mCredits);
        gameState.putInt("creditsEarned", mCreditsEarned);
    }

    @Override
    public void readState(KeyValueStore gameState) {
        mLives = gameState.getInt("lives");
        mCredits = gameState.getInt("credits");
        mCreditsEarned = gameState.getInt("creditsEarned");

        creditsChanged();
        livesChanged();
    }
}
