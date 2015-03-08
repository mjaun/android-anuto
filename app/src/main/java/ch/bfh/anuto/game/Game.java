package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Game implements Runnable {
    /*
    ------ Members ------
     */

    protected final static int MSG_TICK = 1;

    protected final ScheduledThreadPoolExecutor mGameExecutor;
    protected final ArrayList<GameListener> mListeners = new ArrayList<>();

    protected final RectF mGameBounds;
    protected RectF mScreenBounds;
    protected float mBlockLength;

    protected long mTickCount = 0;
    protected final ArrayList<GameObject> mGameObjects = new ArrayList<>();

    /*
    ------ Constructors ------
     */

    public Game(int width, int height) {
        mGameBounds = new RectF(0, 0, width - 1, height - 1);

        // TODO: not sure about how many threads make sense here...
        // TODO: do we have to shutdown this thing?
        mGameExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1);
        mGameExecutor.scheduleAtFixedRate(this, 0, 25, TimeUnit.MILLISECONDS);
    }

    /*
    ------ Public Methods ------
     */

    public void addObject(GameObject object) {
        object.setGame(this);
        mGameObjects.add(object);
    }

    public void removeObject(GameObject object) {
        mGameObjects.remove(object);
        object.setGame(null);
    }

    public List<GameObject> getObjects() {
        // TODO: does it make sense to encapsulate mGameObjects?
        return Collections.unmodifiableList(mGameObjects);
    }

    public long getTickCount() {
        return mTickCount;
    }

    public void calcScreenBounds(int width, int height) {

        float blockWidth = width / (mGameBounds.width() + 1);
        float blockHeight = height / (mGameBounds.height() + 1);

        mBlockLength = Math.min(blockWidth, blockHeight);

        float paddingX = width - (mBlockLength * (mGameBounds.width() + 1));
        float paddingY = height - (mBlockLength * (mGameBounds.height() + 1));

        mScreenBounds = new RectF(0, 0, width, height);
        mScreenBounds.offset(paddingX / 2, paddingY / 2);
    }

    public float getBlockLength() {
        return mBlockLength;
    }

    public PointF getPointOnScreen(PointF gamePoint) {
        float x = mScreenBounds.left + (gamePoint.x + 0.5f) * mBlockLength;
        float y = mScreenBounds.top + (gamePoint.y + 0.5f) * mBlockLength;

        return new PointF(x, y);
    }

    public RectF getBlockOnScreen(PointF gamePoint) {
        float left = mScreenBounds.left + Math.round(gamePoint.x) * mBlockLength;
        float top = mScreenBounds.top + Math.round(gamePoint.y) * mBlockLength;

        return new RectF(left, top, left + mBlockLength, top + mBlockLength);
    }

    public boolean isPointInBounds(PointF gamePoint) {
        return mGameBounds.contains(gamePoint.x, gamePoint.y);
    }

    public synchronized void draw(Canvas canvas) {
        for (GameObject obj : mGameObjects) {
            obj.draw(canvas);
        }
    }

    /*
    ------ Worker Thread Stuff ------
     */

    @Override
    public synchronized void run() {
        try {
            // make a copy so that the original list remains modifiable
            for (GameObject obj : new ArrayList<>(mGameObjects)) {
                obj.tick();
            }

            mTickCount++;
            onTickEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    ------ Handler / Listener Stuff ------
     */

    protected void onTickEvent() {
        for (GameListener listener : mListeners) {
            listener.onTickEvent(this);
        }
    }

    public void addListener(GameListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        mListeners.remove(listener);
    }
}
