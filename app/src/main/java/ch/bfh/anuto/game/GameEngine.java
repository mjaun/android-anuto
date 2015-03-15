package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

public class GameEngine implements Runnable {
    /*
    ------ Constants ------
     */

    private final static String TAG = GameEngine.class.getSimpleName();

    public final static int TARGET_FPS = 30;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000 / TARGET_FPS;

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onRenderRequest();
    }

    /*
    ------ Iterator ------
     */

    private abstract class GameObjectIterator<T extends GameObject> implements Iterator<T>, Iterable<T> {
        private T mNext = null;
        private boolean mNextComputed = false;

        protected abstract T computeNext();

        @Override
        public boolean hasNext() {
            if (!mNextComputed) {
                mNext = computeNext();
                mNextComputed = true;
            }

            return mNext != null;
        }

        @Override
        public T next() {
            if (!mNextComputed) {
                mNext = computeNext();
            }

            if (mNext == null) {
                throw new NoSuchElementException();
            }

            mNextComputed = false;

            return mNext;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    private class GameObjectAllIterator extends GameObjectIterator<GameObject> {
        Iterator<List<GameObject>> mListIterator;
        Iterator<GameObject> mObjectIterator;

        public GameObjectAllIterator() {
            mListIterator = mGameObjects.values().iterator();

            if (mListIterator.hasNext()) {
                mObjectIterator = mListIterator.next().iterator();
            }
        }

        @Override
        public GameObject computeNext() {
            if (mObjectIterator == null) {
                return null;
            }

            while (true) {
                while (!mObjectIterator.hasNext()) {
                    if (mListIterator.hasNext()) {
                        mObjectIterator = mListIterator.next().iterator();
                    } else {
                        return null;
                    }
                }

                GameObject next = mObjectIterator.next();

                if (!next.isRemoved()) {
                    return next;
                }
            }
        }
    }

    private class GameObjectLayerIterator<T extends GameObject> extends GameObjectIterator<T> {
        Iterator<GameObject> mObjectIterator;
        Class<T> mType;

        public GameObjectLayerIterator(Class<T> type, Integer layer) {
            mType = type;

            if (!mGameObjects.containsKey(layer)) {
                mGameObjects.put(layer, new ArrayList<GameObject>());
            }

            mObjectIterator = mGameObjects.get(layer).iterator();
        }

        @Override
        public T computeNext() {
            while (true) {
                if (!mObjectIterator.hasNext()) {
                    return null;
                }

                GameObject next = mObjectIterator.next();

                if (!next.isRemoved()) {
                    return mType.cast(next);
                }
            }
        }
    }

    /*
    ------ Members ------
     */

    private Thread mGameThread;
    private boolean mRunning = false;
    private long mTickCount = 0;

    private final SortedMap<Integer, List<GameObject>> mGameObjects = new TreeMap<>();

    private final Queue<GameObject> mObjectsToAdd = new ArrayDeque<>();
    private final Queue<GameObject> mObjectsToRemove = new ArrayDeque<>();

    private Point mGameSize;
    private Point mScreenSize;
    private float mTileSize;
    private Matrix mScreenMatrix;

    private final List<Listener> mListeners = new ArrayList<>();

    /*
    ------ Constructors ------
     */

    public GameEngine() {
    }

    /*
    ------ Methods ------
     */

    public void addObject(GameObject obj) {
        mObjectsToAdd.add(obj);
        obj.setGame(this);
    }

    public void removeObject(GameObject obj) {
        mObjectsToRemove.add(obj);
        obj.setGame(null);
    }

    public Iterable<GameObject> getObjects() {
        return new GameObjectAllIterator();
    }

    public Iterable<Enemy> getEnemies() {
        return new GameObjectLayerIterator<>(Enemy.class, Enemy.LAYER);
    }

    public void setGameSize(int width, int height) {
        mGameSize = new Point(width, height);

        if (mScreenSize != null) {
            calcScreenMatrix();
        }
    }

    public void setScreenSize(int width, int height) {
        mScreenSize = new Point(width, height);

        if (mGameSize != null) {
            calcScreenMatrix();
        }
    }

    private void calcScreenMatrix() {
        mScreenMatrix = new Matrix();

        mTileSize = Math.min(mScreenSize.x / mGameSize.x, mScreenSize.y / mGameSize.y);
        mScreenMatrix.postTranslate(0.5f, 0.5f);
        mScreenMatrix.postScale(mTileSize, mTileSize);

        float paddingLeft = (mScreenSize.x - (mTileSize * mGameSize.x)) / 2f;
        float paddingTop = (mScreenSize.y - (mTileSize * mGameSize.y)) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);
    }

    public float getTileSize() {
        return mTileSize;
    }

    public long getTickCount() {
        return mTickCount;
    }

    /*
    ------ GameEngine Loop ------
     */

    private synchronized void tick() {
        for (GameObject obj : getObjects()) {
            obj.tick();
        }

        GameObject obj;

        while ((obj = mObjectsToRemove.poll()) != null) {
            mGameObjects.get(obj.getLayer()).remove(obj);
        }

        while ((obj = mObjectsToAdd.poll()) != null) {
            if (!mGameObjects.containsKey(obj.getLayer())) {
                List<GameObject> list = new ArrayList<>();
                list.add(obj);

                mGameObjects.put(obj.getLayer(), list);
            } else {
                mGameObjects.get(obj.getLayer()).add(obj);
            }
        }

        mTickCount++;
    }

    public synchronized void render(Canvas canvas) {
        canvas.concat(mScreenMatrix);

        for (GameObject obj : getObjects()) {
            PointF pos = obj.getPosition();

            if (pos == null) {
                continue;
            }

            canvas.save();
            canvas.translate(pos.x, pos.y);

            obj.draw(canvas);

            canvas.restore();
        }
    }

    public void start() {
        mRunning = true;

        mGameThread = new Thread(this);
        mGameThread.start();
    }

    public void stop() {
        mRunning = false;

        while (true) {
            try {
                mGameThread.join();
                break;
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting game loop");

        // see http://www.javacodegeeks.com/2011/07/android-game-development-game-loop.html

        long beginTime;		// the time when the cycle begun
        long timeDiff;		// the time it took for the cycle to execute
        int sleepTime;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped

        try {
            while (mRunning) {
                beginTime = System.currentTimeMillis();
                framesSkipped = 0;

                // update game logic
                tick();

                // send render request
                onRenderRequest();

                // calculate the required sleep time for this cycle
                timeDiff = System.currentTimeMillis() - beginTime;
                sleepTime = (int)(FRAME_PERIOD - timeDiff);

                if (sleepTime > 0) {
                    // send the thread to sleep
                    Thread.sleep(sleepTime);
                }

                while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    // we need to catch up --> update without rendering
                    tick();

                    // recalculate sleep time
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int)((1 + framesSkipped) * FRAME_PERIOD - timeDiff);

                    framesSkipped++;
                }

                if (framesSkipped > 0) {
                    Log.w(TAG, String.format("rendering of %d frames skipped", framesSkipped));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mRunning = false;
        }

        Log.d(TAG, "Stopping game loop");
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

    private void onRenderRequest() {
        for (Listener l : mListeners) {
            l.onRenderRequest();
        }
    }
}
