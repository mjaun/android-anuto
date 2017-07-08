package ch.logixisland.anuto.business.level;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class GameSpeedManager {

    private static final int MAX_SPEED = 8;

    private final GameEngine mGameEngine;

    private final List<GameSpeedListener> mListeners = new CopyOnWriteArrayList<>();

    private int gameSpeed = 1;

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

        gameSpeed = 1;

        updateGameSpeed();
    }

    public void increaseGameSpeed() {
        if(!canIncreaseSpeed()) return;
        gameSpeed *= 2;
        updateGameSpeed();
    }

    public void decreaseGameSpeed() {
        if(!canDecreaseSpeed()) return;
        gameSpeed /= 2;
        updateGameSpeed();
    }


    public void addListener(GameSpeedListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameSpeedListener listener) {
        mListeners.remove(listener);
    }

    private void updateGameSpeed() {
        mGameEngine.setTicksPerLoop(gameSpeed);

        for (GameSpeedListener listener : mListeners) {
            listener.gameSpeedChangedTo(gameSpeed, canIncreaseSpeed(), canDecreaseSpeed());
        }
    }

    private boolean canDecreaseSpeed() {
        return gameSpeed / 2 >= 1;
    }

    private boolean canIncreaseSpeed() {
        return gameSpeed * 2 <= MAX_SPEED;
    }
}
