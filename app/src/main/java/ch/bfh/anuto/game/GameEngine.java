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
    ------ Members ------
     */

    private Thread mGameThread;
    private boolean mRunning = false;

    private final ConcurrentListMap<Integer, GameObject> mGameObjects = new ConcurrentListMap<>();

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

        obj.setGame(this);
        obj.init(mResources);
    }

    public void removeObject(GameObject obj) {
        mGameObjects.remove(obj.getTypeId(), obj);
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

        mGameObjects.update();
    }

    public synchronized void render(Canvas canvas) {
        canvas.drawColor(BACKGROUND_COLOR);
        canvas.concat(mScreenMatrix);

        for (GameObject obj : mGameObjects.getAll()) {
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
