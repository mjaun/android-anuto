package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameSpeed implements GameStateListener {

    private static final int FAST_FORWARD_SPEED = 4;

    private final GameEngine mGameEngine;
    private final List<GameSpeedListener> mListeners = new CopyOnWriteArrayList<>();

    private boolean mFastForwardActive = false;

    public GameSpeed(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public boolean isFastForwardActive() {
        return mFastForwardActive;
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

        setFastForwardActive(!mFastForwardActive);
    }

    public void addListener(GameSpeedListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameSpeedListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void gameRestart() {
        setFastForwardActive(false);
    }

    @Override
    public void gameOver() {

    }

    private void setFastForwardActive(boolean fastForwardActive) {
        if (mFastForwardActive != fastForwardActive) {
            mFastForwardActive = fastForwardActive;
            mGameEngine.setTicksPerLoop(mFastForwardActive ? FAST_FORWARD_SPEED : 1);

            for (GameSpeedListener listener : mListeners) {
                listener.gameSpeedChanged();
            }
        }
    }
}
