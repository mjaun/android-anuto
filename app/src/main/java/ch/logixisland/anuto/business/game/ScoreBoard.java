package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;
import ch.logixisland.anuto.engine.logic.persistence.Persister;
import ch.logixisland.anuto.util.container.KeyValueStore;

public class ScoreBoard implements Persister {

    public interface BonusListener {
        void bonusChanged(int waveBonus, int earlyBonus);
    }

    public interface CreditsListener {
        void creditsChanged(int credits);
    }

    public interface LivesListener {
        void livesChanged(int lives);
    }

    private final GameSettings mGameSettings;
    private final GameEngine mGameEngine;

    private int mCredits;
    private int mCreditsEarned;
    private int mLives;
    private int mEarlyBonus;
    private int mWaveBonus;

    private final List<CreditsListener> mCreditsListeners = new CopyOnWriteArrayList<>();
    private final List<LivesListener> mLivesListeners = new CopyOnWriteArrayList<>();
    private final List<BonusListener> mBonusListeners = new CopyOnWriteArrayList<>();

    public ScoreBoard(GameSettings gameSettings, GameEngine gameEngine) {
        mGameSettings = gameSettings;
        mGameEngine = gameEngine;
    }

    public void takeLives(final int lives) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    takeLives(lives);
                }
            });
            return;
        }

        mLives -= lives;
        livesChanged();
    }

    public void giveCredits(final int credits, final boolean earned) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    giveCredits(credits, earned);
                }
            });
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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    takeCredits(credits);
                }
            });
            return;
        }

        mCredits -= credits;
        creditsChanged();
    }

    public void setEarlyBonus(final int earlyBonus) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    setEarlyBonus(earlyBonus);
                }
            });
            return;
        }

        mEarlyBonus = earlyBonus;
        bonusChanged();
    }

    public void setWaveBonus(final int waveBonus) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    setWaveBonus(waveBonus);
                }
            });
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

    public void addBonusListener(BonusListener listener) {
        mBonusListeners.add(listener);
    }

    public void removeBonusListener(BonusListener listener) {
        mBonusListeners.remove(listener);
    }

    public void addCreditsListener(CreditsListener listener) {
        mCreditsListeners.add(listener);
    }

    public void removeCreditsListener(CreditsListener listener) {
        mCreditsListeners.remove(listener);
    }

    public void addLivesListener(LivesListener listener) {
        mLivesListeners.add(listener);
    }

    public void removeLivesListener(LivesListener listener) {
        mLivesListeners.remove(listener);
    }

    private void bonusChanged() {
        for (BonusListener listener : mBonusListeners) {
            listener.bonusChanged(mWaveBonus, mEarlyBonus);
        }
    }

    private void creditsChanged() {
        for (CreditsListener listener : mCreditsListeners) {
            listener.creditsChanged(mCredits);
        }
    }

    private void livesChanged() {
        for (LivesListener listener : mLivesListeners) {
            listener.livesChanged(mLives);
        }
    }

    @Override
    public void resetState(KeyValueStore gameConfig) {
        mLives = mGameSettings.getStartLives();
        mCredits = mGameSettings.getStartCredits();
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
    public void readState(KeyValueStore gameConfig, KeyValueStore gameState) {
        mLives = gameState.getInt("lives");
        mCredits = gameState.getInt("credits");
        mCreditsEarned = gameState.getInt("creditsEarned");

        creditsChanged();
        livesChanged();
    }
}
