package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameSpeed {
    private static final int MIN_FAST_FORWARD_SPEED = 2;
    private static final int MAX_FAST_FORWARD_SPEED = 32;

    public interface Listener {
        void gameSpeedChanged();
    }

    private final GameEngine mGameEngine;
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mFastForwardActive = false;
    private int mFastForwardMultiplier = MIN_FAST_FORWARD_SPEED;

    public GameSpeed(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public boolean isFastForwardActive() {
        return mFastForwardActive;
    }

    public void setFastForwardActive(final boolean active) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(() -> setFastForwardActive(active));
            return;
        }

        if (mFastForwardActive != active) {
            mFastForwardActive = active;
            updateTicks();
        }
    }

    public int fastForwardMultiplier() {
        return mFastForwardMultiplier;
    }

    public void cycleFastForward() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(this::cycleFastForward);
            return;
        }

        mFastForwardMultiplier = mFastForwardMultiplier < MAX_FAST_FORWARD_SPEED ? mFastForwardMultiplier * 2 : MIN_FAST_FORWARD_SPEED;

        updateTicks();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void updateTicks() {
        if (mFastForwardActive)
            mGameEngine.setTicksPerLoop(mFastForwardMultiplier);
        else
            mGameEngine.setTicksPerLoop(1);

        for (Listener listener : mListeners) {
            listener.gameSpeedChanged();
        }
    }
}
