package ch.logixisland.anuto.business.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.loop.Message;

public class GameSpeed {

    private static final int SPEED_LEVELS[] = {1, 4, 8};

    public interface Listener {
        void gameSpeedChanged();
    }

    private final GameEngine mGameEngine;
    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    private int mCurrentSpeed = SPEED_LEVELS[0];

    public GameSpeed(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    public int getCurrentSpeed() {
        return mCurrentSpeed;
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

        int index = 0;
        for (int level : SPEED_LEVELS) {
            if (level == mCurrentSpeed) {
                index++;
                break;
            }
            index++;
        }
        setFastForwardActive(SPEED_LEVELS[index % SPEED_LEVELS.length]);
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void setFastForwardActive(int currentSpeed) {
        if (mCurrentSpeed != currentSpeed) {
            mCurrentSpeed = currentSpeed;
            mGameEngine.setTicksPerLoop(mCurrentSpeed);

            for (Listener listener : mListeners) {
                listener.gameSpeedChanged();
            }
        }
    }
}
