package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;

public class GameSpeed {
    private static final int FAST_FORWARD_SPEED = 2;
    private static final int MAX_FAST_FORWARD_SPEED = 128;

    public interface Listener {
        void gameSpeedChanged();
    }

    private final GameEngine mGameEngine;
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mFastForwardActive = false;
    private int mFastForwardMultiplier = FAST_FORWARD_SPEED;

    public GameSpeed(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public boolean isFastForwardActive() {
        return mFastForwardActive;
    }

    public void setFastForwardActive(final boolean active) {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    setFastForwardActive(active);
                }
            });
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
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    cycleFastForward();
                }
            });
            return;
        }

        cycleThroughMultiplier();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void cycleThroughMultiplier() {
        mFastForwardMultiplier = mFastForwardMultiplier < MAX_FAST_FORWARD_SPEED ? mFastForwardMultiplier * 2 : FAST_FORWARD_SPEED;

        updateTicks();
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
