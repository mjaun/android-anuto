package ch.logixisland.anuto.business.level;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameSpeedManager {

    private static final int MAX_SPEED = 8;

    private final GameEngine mGameEngine;
    private final List<GameSpeedListener> mListeners = new CopyOnWriteArrayList<>();

    private int mGameSpeed = 1;

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

        mGameSpeed = 1;
        updateGameSpeed();
    }

    public void increaseGameSpeed() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    increaseGameSpeed();
                }
            });
            return;
        }

        if (!canIncreaseSpeed()) {
            return;
        }

        mGameSpeed *= 2;
        updateGameSpeed();
    }

    public void decreaseGameSpeed() {
        if (mGameEngine.isThreadChangeNeeded()) {
            mGameEngine.post(new Runnable() {
                @Override
                public void run() {
                    decreaseGameSpeed();
                }
            });
            return;
        }

        if (!canDecreaseSpeed()) {
            return;
        }

        mGameSpeed /= 2;
        updateGameSpeed();
    }

    public void addListener(GameSpeedListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameSpeedListener listener) {
        mListeners.remove(listener);
    }

    private void updateGameSpeed() {
        mGameEngine.setTicksPerLoop(mGameSpeed);

        for (GameSpeedListener listener : mListeners) {
            listener.gameSpeedChanged(mGameSpeed, canIncreaseSpeed(), canDecreaseSpeed());
        }
    }

    private boolean canDecreaseSpeed() {
        return mGameSpeed / 2 >= 1;
    }

    private boolean canIncreaseSpeed() {
        return mGameSpeed * 2 <= MAX_SPEED;
    }
}
