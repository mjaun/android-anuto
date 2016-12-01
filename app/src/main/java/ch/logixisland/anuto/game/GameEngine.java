package ch.logixisland.anuto.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Random;

import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.render.Drawable;
import ch.logixisland.anuto.util.container.SmartIteratorCollection;
import ch.logixisland.anuto.util.container.SparseCollectionArray;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.vector.Vector2;
import ch.logixisland.anuto.game.theme.Theme;

public class GameEngine implements Runnable {

    /*
    ------ Constants ------
     */

    public final static int TARGET_FRAME_RATE = 30;
    private final static int TARGET_FRAME_PERIOD_MS = 1000 / TARGET_FRAME_RATE;
    private final static int TICKS_100MS = Math.round(TARGET_FRAME_RATE * 0.1f);

    private final static String TAG = GameEngine.class.getSimpleName();

    /*
    ------ Static ------
     */

    private static GameEngine sInstance;

    public static GameEngine getInstance() {
        if (sInstance == null) {
            sInstance = new GameEngine();
        }

        return sInstance;
    }

    /*
    ------ Members ------
     */

    private Thread mGameThread;

    private volatile boolean mRunning = false;
    private long mTickCount = 0;
    private int mMaxTickTime;
    private int mMaxRenderTime;

    private final SparseCollectionArray<Entity> mEntities = new SparseCollectionArray<>();
    private final SparseCollectionArray<Drawable> mDrawables = new SparseCollectionArray<>();
    private final HashMap<Class<? extends Entity>, Object> mStaticData = new HashMap<>();

    private final Vector2 mGameSize = new Vector2(10, 10);
    private final Vector2 mScreenSize = new Vector2(100, 100);
    private final Matrix mScreenMatrix = new Matrix();
    private final Matrix mScreenMatrixInverse = new Matrix();

    private View mView;
    private Theme mTheme;
    private Resources mResources;

    private final SmartIteratorCollection<Runnable> mRunnables = new SmartIteratorCollection<>();

    /*
    ------ Constructors ------
     */

    private GameEngine() {
        setTheme(Theme.getDefaultTheme());
    }

    /*
    ------ Methods ------
     */

    public Resources getResources() {
        return mResources;
    }

    public void setResources(Resources res) {
        mResources = res;
    }

    public void setView(View view) {
        mView = view;
    }

    public void setTheme(int dt) {
        setTheme(Theme.getTheme(dt));
    }

    public Theme getTheme() { return mTheme; }

    public void setTheme(Theme theme) {
        this.mTheme = theme;
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


    public Vector2 getGameSize() {
        return new Vector2(mGameSize);
    }

    public void setGameSize(int width, int height) {
        mGameSize.set(width, height);
        calcScreenMatrix();
    }

    public void setScreenSize(int width, int height) {
        mScreenSize.set(width, height);
        calcScreenMatrix();
    }

    public Vector2 screenToGame(Vector2 pos) {
        float[] pts = {pos.x, pos.y};
        mScreenMatrixInverse.mapPoints(pts);
        return new Vector2(pts[0], pts[1]);
    }

    public boolean inGame(Vector2 pos) {
        return pos.x >= -0.5f && pos.y >= -0.5f &&
                pos.x < mGameSize.x + 0.5f && pos.y < mGameSize.y + 0.5f;
    }


    private void calcScreenMatrix() {
        mScreenMatrix.reset();

        float tileSize = Math.min(mScreenSize.x / mGameSize.x, mScreenSize.y / mGameSize.y);
        mScreenMatrix.postTranslate(0.5f, 0.5f);
        mScreenMatrix.postScale(tileSize, tileSize);

        float paddingLeft = (mScreenSize.x - (tileSize * mGameSize.x)) / 2f;
        float paddingTop = (mScreenSize.y - (tileSize * mGameSize.y)) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);

        mScreenMatrix.postScale(1f, -1f);
        mScreenMatrix.postTranslate(0, mScreenSize.y);

        mScreenMatrix.invert(mScreenMatrixInverse);
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

                synchronized (mDrawables) {
                    mView.postInvalidate();
                    mDrawables.wait();
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
        canvas.drawColor(mTheme.getBackgroundColor());
        canvas.concat(mScreenMatrix);

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
