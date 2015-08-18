package ch.logixisland.anuto.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.util.container.ConcurrentCollectionMap;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Vector2;

public class GameEngine {

    /*
    ------ Constants ------
     */

    public final static int TARGET_FRAME_RATE = 30;
    public final static int TARGET_FRAME_PERIOD_MS = 1000 / TARGET_FRAME_RATE;

    private final static int BACKGROUND_COLOR = Color.WHITE;

    private final static String TAG = GameEngine.class.getSimpleName();

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onTick();
    }

    /*
    ------ Helper Classes ------
     */

    private class GameObjectMap extends ConcurrentCollectionMap<Integer, GameObject> {
        @Override
        public boolean add(Integer key, GameObject value) {
            boolean ret = super.add(key, value);

            if (ret) {
                value.init();
            }

            return ret;
        }

        @Override
        public boolean remove(Integer key, GameObject value) {
            boolean ret = super.remove(key, value);

            if (ret) {
                value.clean();
            }

            return ret;
        }

        @Override
        protected int compareKeys(Integer k1, Integer k2) {
            if (k1 > k2) return -1;
            if (k1 < k2) return 1;
            return 0;
        }
    }

    private class DrawObjectMap extends ConcurrentCollectionMap<Integer, DrawObject> {
        @Override
        protected int compareKeys(Integer k1, Integer k2) {
            if (k1 > k2) return -1;
            if (k1 < k2) return 1;
            return 0;
        }
    }

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

    private HandlerThread mGameThread;
    private Handler mGameHandler;
    private boolean mRunning = false;

    private int mLastTickTime;
    private int mLastRenderTime;
    private long mTickCount = 0;
    private long mRenderCount = 0;

    private final GameObjectMap mGameObjects = new GameObjectMap();
    private final DrawObjectMap mDrawObjects = new DrawObjectMap();

    private final Vector2 mGameSize = new Vector2(10, 10);
    private final Vector2 mScreenSize = new Vector2(100, 100);
    private final Matrix mScreenMatrix = new Matrix();
    private final Matrix mScreenMatrixInverse = new Matrix();

    private Resources mResources;
    private final Random mRandom = new Random();

    private final List<Listener> mListeners = new CopyOnWriteArrayList<>();

    private final TickTimer mTimer100ms = new TickTimer() {
        long mLastTick = -1;
        boolean mLastResult;

        @Override
        public boolean tick() {
            if (mLastTick == mTickCount) {
                return mLastResult;
            }
            mLastTick = mTickCount;

            mLastResult = super.tick();
            return mLastResult;
        }
    };

    /*
    ------ Constructors ------
     */

    private GameEngine() {
        mTimer100ms.setInterval(0.1f);
        calcScreenMatrix();
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

    public Random getRandom() {
        return mRandom;
    }

    public int getRandom(int max) {
        return mRandom.nextInt(max);
    }

    public int getRandom(int min, int max) {
        return mRandom.nextInt(max - min) + min;
    }

    public float getRandom(float max) {
        return mRandom.nextFloat() * max;
    }

    public float getRandom(float min, float max) {
        return mRandom.nextFloat() * (max - min) + min;
    }

    public long getTickCount() {
        return mTickCount;
    }

    public TickTimer getTimer100ms() {
        return mTimer100ms;
    }


    public void add(GameObject obj) {
        mGameObjects.add(obj.getTypeId(), obj);
    }

    public void remove(GameObject obj) {
        mGameObjects.remove(obj.getTypeId(), obj);
    }

    public void add(DrawObject obj) {
        mDrawObjects.add(obj.getLayer(), obj);
    }

    public void remove(DrawObject obj) {
        mDrawObjects.remove(obj.getLayer(), obj);
    }

    public void clear() {
        for (GameObject obj : mGameObjects) {
            mGameObjects.remove(obj.getTypeId(), obj);
        }
    }


    public StreamIterator<GameObject> getGameObjects() {
        return mGameObjects.iterator();
    }

    public StreamIterator<GameObject> getGameObjects(int typeId) {
        return mGameObjects.iteratorKey(typeId);
    }


    public Vector2 getGameSize() {
        return new Vector2(mGameSize);
    }

    public void setGameSize(int width, int height) {
        mGameSize.set(width, height);
        calcScreenMatrix();
    }

    public Vector2 getScreenSize() {
        return new Vector2(mScreenSize);
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

    public Vector2 gameToScreen(Vector2 pos) {
        float[] pts = {pos.x, pos.y};
        mScreenMatrix.mapPoints(pts);
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

    public void tick() {
        try {
            long beginTime = System.currentTimeMillis();

            mTimer100ms.tick();

            for (GameObject obj : mGameObjects) {
                obj.tick();
            }

            onTick();

            if (mTickCount % (TARGET_FRAME_RATE * 5) == 0) {
                Log.d(TAG, String.format("TT=%d ms, RT=%d ms, TC-RC=%d",
                        mLastTickTime, mLastRenderTime, mTickCount - mRenderCount));
            }

            mTickCount++;
            mLastTickTime = (int) (System.currentTimeMillis() - beginTime);

            int sleepTime = TARGET_FRAME_PERIOD_MS - mLastTickTime;

            if (sleepTime < 0) {
                Log.w(TAG, "Frame did not finish in time!");
                mGameHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tick();
                    }
                });
            } else {
                mGameHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tick();
                    }
                }, sleepTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Canvas canvas) {
        long beginTime = System.currentTimeMillis();

        canvas.drawColor(BACKGROUND_COLOR);
        canvas.concat(mScreenMatrix);

        for (DrawObject obj : mDrawObjects) {
            obj.draw(canvas);
        }

        mRenderCount++;
        mLastRenderTime = (int) (System.currentTimeMillis() - beginTime);
    }


    public void start() {
        if (!mRunning) {
            mRunning = true;

            Log.i(TAG, "Starting game loop");

            mGameThread = new HandlerThread("GameThread-0");
            mGameThread.start();

            mGameHandler = new Handler(mGameThread.getLooper());
            mGameHandler.post(new Runnable() {
                @Override
                public void run() {
                    tick();
                }
            });
        }
    }

    public void stop() {
        if (mRunning) {
            mRunning = false;

            Log.i(TAG, "Stopping game loop");

            mGameThread.quit();

            mGameThread = null;
            mGameHandler = null;
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    public Handler getHandler() {
        return mGameHandler;
    }

    /*
    ------ Listener Stuff ------
     */

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    private void onTick() {
        for (Listener l : mListeners) {
            l.onTick();
        }
    }
}
