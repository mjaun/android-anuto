package ch.logixisland.anuto.business.level;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameSpeedManager {

    private static final int FAST_FORWARD_SPEED = 8;

    private final GameEngine mGameEngine;
    private final List<GameSpeedListener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mFastForwardActive = false;

    public GameSpeedManager(GameEngine gameEngine){
        mGameEngine = gameEngine;
    }

    public void reset() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            });
            return;
        }

        mFastForwardActive = false;
        updateGameSpeed();
    }

    public void toggleFastForward() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    toggleFastForward();
                }
            });
            return;
        }

        mFastForwardActive = !mFastForwardActive;
        updateGameSpeed();
    }

    public void addListener(GameSpeedListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameSpeedListener listener) {
        mListeners.remove(listener);
    }

    private void updateGameSpeed() {
        mGameEngine.setTicksPerLoop(mFastForwardActive ? FAST_FORWARD_SPEED : 1);

        for (GameSpeedListener listener : mListeners) {
            listener.gameSpeedChanged(mFastForwardActive);
        }
    }

}
