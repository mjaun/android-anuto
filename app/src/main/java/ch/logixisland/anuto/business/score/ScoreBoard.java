package ch.logixisland.anuto.business.score;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.Message;

public class ScoreBoard {

    private final GameEngine mGameEngine;

    private int mCredits;
    private int mCreditsEarned;
    private int mLives;
    private int mEarlyBonus;
    private int mWaveBonus;

    private final List<CreditsListener> mCreditsListeners = new CopyOnWriteArrayList<>();
    private final List<LivesListener> mLivesListeners = new CopyOnWriteArrayList<>();
    private final List<BonusListener> mBonusListeners = new CopyOnWriteArrayList<>();

    public ScoreBoard(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public void reset(final int lives, final int credits) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    reset(lives, credits);
                }
            });
            return;
        }

        mCredits = credits;
        mCreditsEarned = 0;
        mLives = lives;
        mEarlyBonus = 0;
        mWaveBonus = 0;

        creditsChanged();
        livesChanged();
        bonusChanged();
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
}
