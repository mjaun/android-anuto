package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.util.container.MultiMap;

public class Renderer implements ThemeListener {

    private static final String TAG = Renderer.class.getSimpleName();

    private static final int LOG_INTERVAL = 5;

    private final Viewport mViewport;
    private final ThemeManager mThemeManager;
    private final MultiMap<Drawable> mDrawables = new MultiMap<>();
    private final Lock mLock = new ReentrantLock(true);

    private final Handler mDebugHandler;
    private final AtomicInteger mUpdateCount = new AtomicInteger();
    private final AtomicInteger mRenderCount = new AtomicInteger();

    private final Runnable mDebugOutput = new Runnable() {
        @Override
        public void run() {
            int updateCount = mUpdateCount.getAndSet(0) / LOG_INTERVAL;
            int renderCount = mRenderCount.getAndSet(0) / LOG_INTERVAL;

            Log.d(TAG, String.format("update: %1$sHz; render: %2$sHz", updateCount, renderCount));
            mDebugHandler.postDelayed(this, LOG_INTERVAL * 1000);
        }
    };

    private int mBackgroundColor;
    private WeakReference<View> mViewRef;

    public Renderer(Viewport viewport, ThemeManager themeManager) {
        mViewport = viewport;
        mThemeManager = themeManager;
        mThemeManager.addListener(this);
        themeChanged();

        mDebugHandler = new Handler();
        mDebugHandler.post(mDebugOutput);
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
        mUpdateCount.incrementAndGet();
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

    public void draw(Canvas canvas) {
        mLock.lock();
        mRenderCount.incrementAndGet();

        canvas.drawColor(mBackgroundColor);
        canvas.concat(mViewport.getScreenMatrix());

        for (Drawable obj : mDrawables) {
            obj.draw(canvas);
        }

        mLock.unlock();
    }

    @Override
    public void themeChanged() {
        mBackgroundColor = mThemeManager.getColor(R.attr.backgroundColor);
    }
}
