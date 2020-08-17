package ch.logixisland.anuto.engine.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.logixisland.anuto.engine.logic.loop.FrameRateLogger;
import ch.logixisland.anuto.util.container.SafeMultiMap;
import ch.logixisland.anuto.util.math.Vector2;

public class Renderer {

    private final Viewport mViewport;
    private final FrameRateLogger mFrameRateLogger;
    private final SafeMultiMap<Drawable> mDrawables = new SafeMultiMap<>();
    private final Lock mLock = new ReentrantLock(true);

    private int mBackgroundColor;
    private WeakReference<View> mViewRef;

    public Renderer(Viewport viewport, FrameRateLogger frameRateLogger) {
        mViewport = viewport;
        mFrameRateLogger = frameRateLogger;
        mViewRef = new WeakReference<>(null);
    }

    public void setView(final View view) {
        mViewRef = new WeakReference<>(view);
    }

    public void add(Drawable obj) {
        mDrawables.add(obj.getLayer(), obj);
    }

    public void remove(Drawable obj) {
        mDrawables.remove(obj.getLayer(), obj);
    }

    public void clear() {
        mDrawables.clear();
    }

    public void lock() {
        mLock.lock();
    }

    public void unlock() {
        mLock.unlock();
    }

    public void invalidate() {
        View view = mViewRef.get();

        if (view != null) {
            view.postInvalidate();
        }
    }

    public Bitmap getScreenshot() {
        RectF mapRect = mViewport.getScreenGameRect();
        Bitmap bitmap = Bitmap.createBitmap(Math.round(mapRect.width()), Math.round(mapRect.height()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    public void draw(Canvas canvas) {
        mLock.lock();

        canvas.drawColor(Color.BLACK);
        canvas.concat(mViewport.getScreenMatrix());
        canvas.clipRect(mViewport.getGameClipRect());
        canvas.drawColor(mBackgroundColor);

        for (Drawable obj : mDrawables) {
            obj.draw(canvas);
        }

        mLock.unlock();

        mFrameRateLogger.incrementRenderCount();
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public boolean isPositionVisible(Vector2 position) {
        return mViewport.getGameClipRect().contains(position.x(), position.y());
    }
}
