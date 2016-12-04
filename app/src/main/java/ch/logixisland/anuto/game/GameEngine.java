package ch.logixisland.anuto.game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.render.Drawable;
import ch.logixisland.anuto.game.render.Renderer;
import ch.logixisland.anuto.game.render.Viewport;
import ch.logixisland.anuto.game.render.theme.ThemeManager;
import ch.logixisland.anuto.util.container.SmartIteratorCollection;
import ch.logixisland.anuto.util.container.SparseCollectionArray;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class GameEngine implements Runnable {

    /*
    ------ Constants ------
     */

    public final static int TARGET_FRAME_RATE = 30;
    private final static int TARGET_FRAME_PERIOD_MS = 1000 / TARGET_FRAME_RATE;
    private final static int TICKS_100MS = Math.round(TARGET_FRAME_RATE * 0.1f);

    private final static String TAG = GameEngine.class.getSimpleName();

    /*
    ------ Members ------
     */

    private final Renderer mRenderer;

    private final SparseCollectionArray<Entity> mEntities = new SparseCollectionArray<>();
    private final HashMap<Class<? extends Entity>, Object> mStaticData = new HashMap<>();
    private final SmartIteratorCollection<Runnable> mRunnables = new SmartIteratorCollection<>();

    private Thread mGameThread;
    private volatile boolean mRunning = false;
    private long mTickCount = 0;

    /*
    ------ Constructors ------
     */

    GameEngine(Renderer renderer) {
        mRenderer = renderer;
    }

    /*
    ------ Methods ------
     */

    public boolean tick100ms(Object caller) {
        return (mTickCount + System.identityHashCode(caller)) % TICKS_100MS == 0;
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

    public void add(Entity obj) {
        synchronized (mEntities) {
            mEntities.add(obj.getType(), obj);
            obj.init();
        }
    }

    public void add(Drawable obj) {
        mRenderer.add(obj);
    }

    public void remove(Entity obj) {
        synchronized (mEntities) {
            mEntities.remove(obj.getType(), obj);
            obj.clean();
        }
    }

    public void remove(Drawable obj) {
        mRenderer.remove(obj);
    }

    public void add(Runnable r) {
        synchronized (mEntities) {
            mRunnables.add(r);
        }
    }

    public void remove(Runnable r) {
        synchronized (mEntities) {
            mRunnables.remove(r);
        }
    }

    public void clear() {
        synchronized (mEntities) {
            for (Entity obj : mEntities) {
                mEntities.remove(obj.getType(), obj);
                obj.clean();
            }

            mRunnables.clear();
            mStaticData.clear();
            mRenderer.clear();
        }
    }

    /*
    ------ GameEngine Loop ------
     */

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

    @Override
    public void run() {
        long lastLogTick = 0;

        try {
            while (mRunning) {
                long timeTickBegin = System.currentTimeMillis();

                synchronized (mEntities) {
                    for (Runnable r : mRunnables) {
                        r.run();
                    }

                    for (Entity obj : mEntities) {
                        obj.tick();
                    }
                }

                mRenderer.render();
                mTickCount++;

                long timeTickFinished = System.currentTimeMillis();

                int sleepTime = TARGET_FRAME_PERIOD_MS - (int)(timeTickFinished - timeTickBegin);

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
