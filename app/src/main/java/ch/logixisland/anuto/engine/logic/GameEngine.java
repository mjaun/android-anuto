package ch.logixisland.anuto.engine.logic;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.util.container.SmartIteratorCollection;
import ch.logixisland.anuto.util.container.SparseCollectionArray;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class GameEngine implements Runnable {

    public final static int TARGET_FRAME_RATE = 30;
    private final static String TAG = GameEngine.class.getSimpleName();

    private final Renderer mRenderer;

    private final SparseCollectionArray<Entity> mEntities = new SparseCollectionArray<>();
    private final Map<Class<? extends Entity>, Object> mStaticData = new HashMap<>();
    private final Collection<TickListener> mTickListeners = new SmartIteratorCollection<>();
    private final MessageQueue mMessageQueue = new MessageQueue();

    private Thread mGameThread;
    private volatile boolean mRunning = false;
    private long mTickCount = 0;

    public GameEngine(Renderer renderer) {
        mRenderer = renderer;
    }

    public Object getStaticData(Entity obj) {
        if (!mStaticData.containsKey(obj.getClass())) {
            mStaticData.put(obj.getClass(), obj.initStatic());
        }

        return mStaticData.get(obj.getClass());
    }

    public StreamIterator<Entity> get(int typeId) {
        synchronized (mEntities) {
            return mEntities.get(typeId).iterator();
        }
    }

    public void add(Entity entity) {
        synchronized (mEntities) {
            mEntities.add(entity.getType(), entity);
            entity.init();
        }
    }

    public void add(Drawable drawable) {
        mRenderer.add(drawable);
    }

    public void add(TickListener listener) {
        synchronized (mEntities) {
            mTickListeners.add(listener);
        }
    }

    public void remove(Entity entity) {
        synchronized (mEntities) {
            mEntities.remove(entity.getType(), entity);
            entity.clean();
        }
    }

    public void remove(Drawable drawable) {
        mRenderer.remove(drawable);
    }

    public void remove(TickListener listener) {
        synchronized (mEntities) {
            mTickListeners.remove(listener);
        }
    }

    public void clear() {
        synchronized (mEntities) {
            for (Entity obj : mEntities) {
                mEntities.remove(obj.getType(), obj);
                obj.clean();
            }

            mMessageQueue.clear();
            mTickListeners.clear();
            mStaticData.clear();
            mRenderer.clear();
        }
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
        postDelayed(runnable, 0);
    }

    public void postDelayed(Runnable runnable, float delay) {
        mMessageQueue.post(runnable, (int)(delay * TARGET_FRAME_RATE));
    }

    public boolean isThreadChangeNeeded() {
        return Thread.currentThread() != mGameThread;
    }

    @Override
    public void run() {
        long lastLogTick = 0;

        try {
            while (mRunning) {
                long timeTickBegin = System.currentTimeMillis();

                synchronized (mEntities) {
                    for (TickListener listener : mTickListeners) {
                        listener.tick();
                    }

                    for (Entity entity : mEntities) {
                        entity.tick();
                    }
                }

                mMessageQueue.tick();
                mRenderer.render();
                mTickCount++;

                long timeTickFinished = System.currentTimeMillis();

                int sleepTime = 1000 / TARGET_FRAME_RATE - (int)(timeTickFinished - timeTickBegin);

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                } else if (sleepTime < 0 && mTickCount - lastLogTick > TARGET_FRAME_RATE) {
                    Log.d(TAG, "Frame did not finish in time!");
                    lastLogTick = mTickCount;
                }
            }
        } catch (Exception e) {
            mRunning = false;
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
