package ch.logixisland.anuto.engine.logic;

import android.util.Log;

import java.util.Collection;

import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.util.container.SafeCollection;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class GameEngine implements Runnable {

    private final static String TAG = GameEngine.class.getSimpleName();

    public final static int TARGET_FRAME_RATE = 30;
    private final static int TICK_TIME = 1000 / TARGET_FRAME_RATE;
    private final static int MAX_FRAME_SKIPS = 1;

    private final EntityStore mEntityStore;
    private final MessageQueue mMessageQueue;
    private final Renderer mRenderer;
    private final FrameRateLogger mFrameRateLogger;

    private final Collection<TickListener> mTickListeners = new SafeCollection<>();

    private int mGameTicksPerLoop = 1;

    private Thread mGameThread;
    private volatile boolean mRunning = false;

    public GameEngine(EntityStore entityStore, MessageQueue messageQueue, Renderer renderer,
                      FrameRateLogger frameRateLogger) {
        mEntityStore = entityStore;
        mMessageQueue = messageQueue;
        mRenderer = renderer;
        mFrameRateLogger = frameRateLogger;
    }

    public Object getStaticData(Entity entity) {
        return mEntityStore.getStaticData(entity);
    }

    public StreamIterator<Entity> get(int typeId) {
        return mEntityStore.get(typeId);
    }

    public void add(Entity entity) {
        mEntityStore.add(entity);
    }

    public void add(Drawable drawable) {
        mRenderer.add(drawable);
    }

    public void add(TickListener listener) {
        mTickListeners.add(listener);
    }

    public void remove(Entity entity) {
        mEntityStore.remove(entity);
    }

    public void remove(Drawable drawable) {
        mRenderer.remove(drawable);
    }

    public void remove(TickListener listener) {
        mTickListeners.remove(listener);
    }

    public void clear() {
        mMessageQueue.clear();
        mTickListeners.clear();
        mEntityStore.clear();
        mRenderer.clear();
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
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void post(Runnable runnable) {
        mMessageQueue.post(runnable);
    }

    public void postDelayed(Runnable runnable, float delay) {
        mMessageQueue.postDelayed(runnable, (int) (delay * TARGET_FRAME_RATE));
    }

    public void setTicksPerLoop(int newTicksPerLoopValue) {
        mGameTicksPerLoop = newTicksPerLoopValue;
    }

    public boolean isThreadChangeNeeded() {
        return Thread.currentThread() != mGameThread;
    }

    @Override
    public void run() {
        long timeNextTick = System.currentTimeMillis();
        long timeCurrent;
        int skipFrameCount = 0;

        try {
            while (mRunning) {
                timeNextTick += TICK_TIME;

                mRenderer.lock();
                for (int repeat = 0; repeat < mGameTicksPerLoop; repeat++) {
                    executeTick();
                }
                mRenderer.unlock();

                timeCurrent = System.currentTimeMillis();
                int sleepTime = (int) (timeNextTick - timeCurrent);

                if (sleepTime > 0 || skipFrameCount >= MAX_FRAME_SKIPS) {
                    mRenderer.invalidate();
                    skipFrameCount = 0;
                } else {
                    skipFrameCount++;
                }

                mFrameRateLogger.incrementLoopCount();

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                } else {
                    timeNextTick = timeCurrent; // resync
                }
            }
        } catch (Exception e) {
            mRunning = false;
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void executeTick() {
        mEntityStore.tick();

        for (TickListener listener : mTickListeners) {
            listener.tick();
        }

        mMessageQueue.tick();
    }

}
