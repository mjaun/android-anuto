package ch.logixisland.anuto.game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.render.Drawable;
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

    private final ThemeManager mThemeManager;
    private final Viewport mViewport;

    private final SparseCollectionArray<Entity> mEntities = new SparseCollectionArray<>();
    private final SparseCollectionArray<Drawable> mDrawables = new SparseCollectionArray<>();
    private final HashMap<Class<? extends Entity>, Object> mStaticData = new HashMap<>();
    private final SmartIteratorCollection<Runnable> mRunnables = new SmartIteratorCollection<>();

    private WeakReference<View> mViewRef;

    private Thread mGameThread;
    private volatile boolean mRunning = false;
    private long mTickCount = 0;
    private int mMaxTickTime;
    private int mMaxRenderTime;

    /*
    ------ Constructors ------
     */

    GameEngine(ThemeManager themeManager, Viewport viewport) {
        mThemeManager = themeManager;
        mViewport = viewport;
    }

    /*
    ------ Methods ------
     */

    public void setView(View view) {
        mViewRef = new WeakReference<>(view);
    }


    public boolean tick100ms(Object caller) {
        return (mTickCount + System.identityHashCode(caller)) % TICKS_100MS == 0;
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
        synchronized (mDrawables) {
            mDrawables.add(obj.getLayer(), obj);
        }
    }

    public void remove(Entity obj) {
        synchronized (mEntities) {
            mEntities.remove(obj.getType(), obj);
            obj.clean();
        }
    }

    public void remove(Drawable obj) {
        synchronized (mDrawables) {
            mDrawables.remove(obj.getLayer(), obj);
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
        }
    }


    public Object getStaticData(Entity obj) {
        if (!mStaticData.containsKey(obj.getClass())) {
            mStaticData.put(obj.getClass(), obj.initStatic());
        }

        return mStaticData.get(obj.getClass());
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

    /*
    ------ GameEngine Loop ------
     */

    @Override
    public void run() {
        try {
            while (mRunning) {
                long timeTickBegin = System.currentTimeMillis();

                synchronized (mEntities) {
                    for (Runnable r : mRunnables) {
                        r.run();
                    }
                }

                synchronized (mEntities) {
                    for (Entity obj : mEntities) {
                        obj.tick();
                    }
                }

                long timeRenderBegin = System.currentTimeMillis();

                View view = mViewRef.get();
                if (view != null) {
                    synchronized (mDrawables) {
                        mViewRef.get().postInvalidate();
                        mDrawables.wait();
                    }
                }

                long timeFinished = System.currentTimeMillis();

                int tickTime = (int)(timeRenderBegin - timeTickBegin);
                int renderTime = (int)(timeFinished - timeRenderBegin);

                if (tickTime > mMaxTickTime) {
                    mMaxTickTime = tickTime;
                }

                if (renderTime > mMaxRenderTime) {
                    mMaxRenderTime = renderTime;
                }

                if (mTickCount % (TARGET_FRAME_RATE * 5) == 0) {
                    Log.d(TAG, String.format("TT=%d ms, RT=%d ms", mMaxTickTime, mMaxRenderTime));

                    mMaxTickTime = 0;
                    mMaxRenderTime = 0;
                }

                mTickCount++;

                int sleepTime = TARGET_FRAME_PERIOD_MS - tickTime - renderTime;

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
        } catch (InterruptedException e) {
            mRunning = false;
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(mThemeManager.getTheme().getBackgroundColor());
        canvas.concat(mViewport.getScreenMatrix());

        synchronized (mDrawables) {
            for (Drawable obj : mDrawables) {
                obj.draw(canvas);
            }

            mDrawables.notifyAll();
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
}
