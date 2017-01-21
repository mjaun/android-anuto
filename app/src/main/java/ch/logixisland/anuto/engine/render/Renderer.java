package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.util.container.SparseCollectionArray;

public class Renderer {

    private final Viewport mViewport;
    private final ThemeManager mThemeManager;
    private final SparseCollectionArray<Drawable> mDrawables = new SparseCollectionArray<>();
    private final Semaphore mSemaphore = new Semaphore(0);

    private WeakReference<View> mViewRef;

    public Renderer(Viewport viewport, ThemeManager themeManager) {
        mViewport = viewport;
        mThemeManager = themeManager;
    }

    public void setView(View view) {
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

    public void render() throws InterruptedException {
        View view = mViewRef.get();

        if (view != null) {
            view.postInvalidate();

            if (mSemaphore.hasQueuedThreads()) {
                synchronized (mSemaphore) {
                    mSemaphore.release();
                    mSemaphore.wait();
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        try {
            if (!mSemaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
            return;
        }

        canvas.drawColor(mThemeManager.getTheme().getBackgroundColor());
        canvas.concat(mViewport.getScreenMatrix());

        for (Drawable obj : mDrawables) {
            obj.draw(canvas);
        }

        synchronized (mSemaphore) {
            mSemaphore.notify();
        }
    }

}
