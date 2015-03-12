package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine implements Runnable {
    /*
    ------ Constants ------
     */

    private final static String TAG = GameEngine.class.getName();

    public final static int TARGET_FPS = 30;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000 / TARGET_FPS;

    /*
    ------ Members ------
     */

    private final SurfaceHolder mSurfaceHolder;
    private Thread mGameThread;
    private boolean mRunning = false;

    private final ArrayList<GameObject> mGameObjects = new ArrayList<>();
    private final ArrayList<GameObject> mObjectsToAdd = new ArrayList<>();
    private final ArrayList<GameObject> mObjectsToRemove = new ArrayList<>();

    private RectF mGameBounds;
    private RectF mScreenBounds;
    private float mTileLength;

    private final ArrayList<GameListener> mListeners = new ArrayList<>();


    /*
    ------ Constructors ------
     */

    public GameEngine(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    /*
    ------ Public Methods ------
     */

    public void addObject(GameObject obj) {
        mObjectsToAdd.add(obj);
    }

    public void removeObject(GameObject obj) {
        mObjectsToRemove.add(obj);
    }

    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(mGameObjects);
    }

    public void setGameBounds(int width, int height) {
        mGameBounds = new RectF(0, 0, width - 1, height - 1);
    }

    public void setScreenBounds(int width, int height) {

        float blockWidth = width / (mGameBounds.width() + 1);
        float blockHeight = height / (mGameBounds.height() + 1);

        mTileLength = Math.min(blockWidth, blockHeight);

        float paddingX = width - (mTileLength * (mGameBounds.width() + 1));
        float paddingY = height - (mTileLength * (mGameBounds.height() + 1));

        mScreenBounds = new RectF(0, 0, width, height);
        mScreenBounds.offset(paddingX / 2, paddingY / 2);
    }

    public float getTileSize() {
        return mTileLength;
    }

    public PointF getPointOnScreen(PointF gamePoint) {
        float x = mScreenBounds.left + (gamePoint.x + 0.5f) * mTileLength;
        float y = mScreenBounds.top + (gamePoint.y + 0.5f) * mTileLength;

        return new PointF(x, y);
    }

    public RectF getBlockOnScreen(PointF gamePoint) {
        float left = mScreenBounds.left + Math.round(gamePoint.x) * mTileLength;
        float top = mScreenBounds.top + Math.round(gamePoint.y) * mTileLength;

        return new RectF(left, top, left + mTileLength, top + mTileLength);
    }

    /*
    ------ GameEngine Loop ------
     */

    private void tick() {
        for (GameObject obj : mGameObjects) {
            obj.tick();
        }

        for (GameObject obj : mObjectsToAdd) {
            mGameObjects.add(obj);
            obj.setGame(this);
        }

        for (GameObject obj : mObjectsToRemove) {
            mGameObjects.remove(obj);
            obj.setGame(null);
        }

        mObjectsToAdd.clear();
        mObjectsToRemove.clear();
    }

    private void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        for (GameObject obj : mGameObjects) {
            obj.draw(canvas);
        }
    }

    public void start() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    public void shutdown() throws InterruptedException {
        mRunning = false;
        mGameThread.join();
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");

        // see http://www.javacodegeeks.com/2011/07/android-game-development-game-loop.html

        long beginTime;		// the time when the cycle begun
        long timeDiff;		// the time it took for the cycle to execute
        int sleepTime;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped

        try {
            while (mRunning) {
                canvas = null;

                beginTime = System.currentTimeMillis();
                framesSkipped = 0;

                // update game logic
                tick();

                // try locking the canvas for exclusive pixel editing in the surface
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    boolean hw = canvas.isHardwareAccelerated();

                    synchronized (mSurfaceHolder) {
                        // render current game state
                        draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

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

    public void addListener(GameListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        mListeners.remove(listener);
    }
}
