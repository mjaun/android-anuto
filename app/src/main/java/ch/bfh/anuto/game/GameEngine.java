package ch.bfh.anuto.game;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.anuto.util.ConcurrentListMap;
import ch.bfh.anuto.util.Predicate;

public class GameEngine implements Runnable {
    /*
    ------ Constants ------
     */

    public final static int TARGET_FPS = 30;

    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000 / TARGET_FPS;
    private final static int BACKGROUND_COLOR = Color.WHITE;

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

    private class GameObjectListMap extends ConcurrentListMap<Integer, GameObject> {
        @Override
        protected void onItemAdded(Integer key, GameObject value) {
            value.setGame(GameEngine.this);
            value.init(mResources);
        }

        @Override
        protected void onItemRemoved(Integer key, GameObject value) {
            value.clean();
            value.setGame(null);
        }
    }

    /*
    ------ Members ------
     */

    private Thread mGameThread;
    private boolean mRunning = false;

    private final GameObjectListMap mGameObjects = new GameObjectListMap();

    private Point mGameSize;
    private Point mScreenSize;
    private Matrix mScreenMatrix;

    private final Resources mResources;

    private final List<Listener> mListeners = new ArrayList<>();

    /*
    ------ Constructors ------
     */

    public GameEngine(Resources res) {
        mResources = res;
    }

    /*
    ------ Methods ------
     */

    public void addObject(GameObject obj) {
        mGameObjects.addDeferred(obj.getTypeId(), obj);
    }

    public void removeObject(GameObject obj) {
        mGameObjects.removeDeferred(obj.getTypeId(), obj);
    }


    public Iterable<GameObject> getObjects() {
        return mGameObjects.getAll();
    }

    public Iterable<GameObject> getObjects(int typeId) {
        return mGameObjects.getByKey(typeId);
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

        float tileSize = Math.min(mScreenSize.x / mGameSize.x, mScreenSize.y / mGameSize.y);
        mScreenMatrix.postTranslate(0.5f, 0.5f);
        mScreenMatrix.postScale(tileSize, tileSize);

        float paddingLeft = (mScreenSize.x - (tileSize * mGameSize.x)) / 2f;
        float paddingTop = (mScreenSize.y - (tileSize * mGameSize.y)) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);
    }

    /*
    ------ GameEngine Loop ------
     */

    private synchronized void tick() {
        for (GameObject obj : mGameObjects.getAll()) {
            obj.tick();
        }

        mGameObjects.applyChanges();
    }

    public synchronized void render(Canvas canvas) {
        canvas.drawColor(BACKGROUND_COLOR);
        canvas.concat(mScreenMatrix);

        for (GameObject obj : mGameObjects.getAll()) {
            PointF pos = obj.getPosition();

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
