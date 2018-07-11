package ch.logixisland.anuto.engine.logic.loop;

import android.util.Log;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.entity.EntityStore;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.util.container.SafeCollection;

public class GameLoop implements Runnable {

    private final static String TAG = GameLoop.class.getSimpleName();

    public final static int TARGET_FRAME_RATE = 30;
    private final static int TICK_TIME = 1000 / TARGET_FRAME_RATE;
    private final static int MAX_FRAME_SKIPS = 1;

    private final Renderer mRenderer;
    private final FrameRateLogger mFrameRateLogger;
    private final MessageQueue mMessageQueue;
    private final EntityStore mEntityStore;

    private final Collection<TickListener> mTickListeners = new SafeCollection<>();
    private final Collection<ErrorListener> mErrorListeners = new CopyOnWriteArrayList<>();

    private int mGameTicksPerLoop = 1;

    private Thread mGameThread;
    private volatile boolean mRunning = false;

    public GameLoop(Renderer renderer, FrameRateLogger frameRateLogger, MessageQueue messageQueue, EntityStore entityStore) {
        mRenderer = renderer;
        mFrameRateLogger = frameRateLogger;
        mMessageQueue = messageQueue;
        mEntityStore = entityStore;
    }

    public void registerErrorListener(ErrorListener listener) {
        mErrorListeners.add(listener);
    }

    public void add(TickListener listener) {
        mTickListeners.add(listener);
    }

    public void remove(TickListener listener) {
        mTickListeners.remove(listener);
    }

    public void clear() {
        mTickListeners.clear();
    }

    public void start() {
        if (!mRunning) {
            Log.i(TAG, "Starting game loop");
            mRunning = true;
            mGameThread = new Thread(this);
            mGameThread.start();
        }
    }

    public void stop() {
        if (mRunning) {
            Log.i(TAG, "Stopping game loop");
            mRunning = false;

            try {
                mGameThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Could not stop game thread!", e);
            }
        }
    }

    public void setTicksPerLoop(int ticksPerLoop) {
        mGameTicksPerLoop = ticksPerLoop;
    }

    public boolean isThreadChangeNeeded() {
        return Thread.currentThread() != mGameThread;
    }

    @Override
    public void run() {
        long timeNextTick = System.currentTimeMillis();
        int skipFrameCount = 0;
        int loopCount = 0;

        try {
            while (mRunning) {
                executeCycle();

                timeNextTick += TICK_TIME;
                int sleepTime = (int) (timeNextTick - System.currentTimeMillis());

                if (sleepTime > 0 || skipFrameCount >= MAX_FRAME_SKIPS) {
                    mRenderer.invalidate();
                    skipFrameCount = 0;
                } else {
                    skipFrameCount++;
                }

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                } else {
                    timeNextTick = System.currentTimeMillis(); // resync
                }

                loopCount++;
            }

            // process messages a last time (needed to save game just before loop stops)
            mMessageQueue.processMessages();

        } catch (Exception e) {
            mRunning = false;
            notifyErrorListeners(loopCount, e);
            throw new RuntimeException("Error in game loop!", e);
        }
    }

    private void executeCycle() {
        mRenderer.lock();
        for (int i = 0; i < mGameTicksPerLoop; i++) {
            executeTick();
            mMessageQueue.processMessages();
        }
        mRenderer.unlock();

        mFrameRateLogger.incrementLoopCount();
        mFrameRateLogger.outputFrameRate();
    }

    private void executeTick() {
        mMessageQueue.tick();
        mEntityStore.tick();

        for (TickListener listener : mTickListeners) {
            listener.tick();
        }
    }

    private void notifyErrorListeners(int loopCount, Exception e) {
        for (ErrorListener listener : mErrorListeners) {
            listener.error(e, loopCount);
        }
    }

}
