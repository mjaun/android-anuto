package ch.bfh.anuto.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.util.container.DeferredListMap;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.Vector2;

public class GameEngine implements Runnable {
    /*
    ------ Constants ------
     */

    public final static int TARGET_FPS = 30;

    private final static int BACKGROUND_COLOR = Color.WHITE;
    private final static int FRAME_PERIOD = 1000 / TARGET_FPS;

    private final static String TAG = GameEngine.class.getSimpleName();

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onRenderRequest();
    }

    /*
    ------ Helper Classes ------
     */

    private class GameObjectListMap extends DeferredListMap<Integer, GameObject> {
        @Override
        public void addDeferred(Integer key, GameObject value) {
            value.setGame(GameEngine.this);
            value.init(mResources);
            super.addDeferred(key, value);
        }

        @Override
        protected void onItemRemoved(Integer key, GameObject value) {
            super.onItemRemoved(key, value);
            value.clean();
            value.setGame(null);
        }
    }

    private class DrawObjectListMap extends DeferredListMap<Integer, DrawObject> {
        @Override
        protected void onItemAdded(Integer key, DrawObject value) {
            value.setLayer(key);
        }
    }

    /*
    ------ Members ------
     */

    private Thread mGameThread;
    private boolean mRunning = false;

    private final GameObjectListMap mGameObjects = new GameObjectListMap();
    private final DrawObjectListMap mDrawObjects = new DrawObjectListMap();

    private final Vector2 mGameSize = new Vector2(10, 10);
    private final Vector2 mScreenSize = new Vector2(100, 100);
    private final Matrix mScreenMatrix = new Matrix();
    private final Matrix mScreenMatrixInverse = new Matrix();

    private final Resources mResources;

    private final List<Listener> mListeners = new ArrayList<>();

    /*
    ------ Constructors ------
     */

    public GameEngine(Resources res) {
        mResources = res;
        calcScreenMatrix();
    }

    /*
    ------ Methods ------
     */

    public void addGameObject(GameObject obj) {
        mGameObjects.addDeferred(obj.getTypeId(), obj);
    }

    public void removeGameObject(GameObject obj) {
        mGameObjects.removeDeferred(obj.getTypeId(), obj);
    }

    public void addDrawObject(DrawObject obj, int layer) {
        mDrawObjects.addDeferred(obj.getLayer(), obj);
    }

    public void removeDrawObject(DrawObject obj) {
        mDrawObjects.removeDeferred(obj.getLayer(), obj);
    }

    public StreamIterator<GameObject> getGameObjects(int typeId) {
        return mGameObjects.getByKey(typeId);
    }


    public void setGameSize(int width, int height) {
        mGameSize.set(width, height);
        calcScreenMatrix();
    }

    public void setScreenSize(int width, int height) {
        mScreenSize.set(width, height);
        calcScreenMatrix();
    }

    public Vector2 getGameCoordinate(float x, float y) {
        float[] pts = {x, y};
        mScreenMatrixInverse.mapPoints(pts);
        return new Vector2(pts[0], pts[1]);
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

    private synchronized void tick() {
        mGameObjects.applyChanges();

        StreamIterator<GameObject> iterator = mGameObjects.getAll();

        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            obj.tick();
        }

        iterator.close();
    }

    public synchronized void render(Canvas canvas) {
        canvas.drawColor(BACKGROUND_COLOR);
        canvas.concat(mScreenMatrix);

        mDrawObjects.applyChanges();

        StreamIterator<DrawObject> iterator = mDrawObjects.getAll();

        while (iterator.hasNext()) {
            DrawObject obj = iterator.next();

            canvas.save();
            obj.draw(canvas);
            canvas.restore();
        }

        iterator.close();
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
        Log.i(TAG, "Starting game loop");

        try {
            while (mRunning) {
                long beginTime = System.currentTimeMillis();

                tick();
                onRenderRequest();

                long timeDiff = System.currentTimeMillis() - beginTime;
                int sleepTime = (int)(FRAME_PERIOD - timeDiff);

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                } else {
                    Log.w(TAG, "Frame did not finish in time!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mRunning = false;
        }

        Log.i(TAG, "Stopping game loop");
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
