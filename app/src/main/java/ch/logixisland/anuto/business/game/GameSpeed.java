package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;

public class GameSpeed {

    private static final int FAST_FORWARD_SPEED = 4;

    public interface Listener {
        void gameSpeedChanged();
    }

    private final GameEngine mGameEngine;
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mFastForwardActive = false;

    public GameSpeed(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public boolean isFastForwardActive() {
        return mFastForwardActive;
    }

    public void toggleFastForward() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Message() {
                @Override
                public void execute() {
                    toggleFastForward();
                }
            });
            return;
        }

        setFastForwardActive(!mFastForwardActive);
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void setFastForwardActive(boolean fastForwardActive) {
        if (mFastForwardActive != fastForwardActive) {
            mFastForwardActive = fastForwardActive;
            mGameEngine.setTicksPerLoop(mFastForwardActive ? FAST_FORWARD_SPEED : 1);

            for (Listener listener : mListeners) {
                listener.gameSpeedChanged();
            }
        }
    }
}
