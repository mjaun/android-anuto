package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.FrameRateLogger;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.util.container.SafeMultiMap;
import ch.logixisland.anuto.util.math.Vector2;

public class Renderer implements ThemeListener {

    private final Viewport mViewport;
    private final FrameRateLogger mFrameRateLogger;
    private final SafeMultiMap<Drawable> mDrawables = new SafeMultiMap<>();
    private final Lock mLock = new ReentrantLock(true);

    private int mBackgroundColor;
    private WeakReference<View> mViewRef;

    public Renderer(Viewport viewport, ThemeManager themeManager, FrameRateLogger frameRateLogger) {
        mViewport = viewport;
        mFrameRateLogger = frameRateLogger;
        themeManager.addListener(this);
        themeChanged(themeManager.getTheme());
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

    public void draw(Canvas canvas) {
        mLock.lock();

        canvas.drawColor(mBackgroundColor);
        canvas.concat(mViewport.getScreenMatrix());
        canvas.clipRect(mViewport.getScreenClipRect());

        for (Drawable obj : mDrawables) {
            obj.draw(canvas);
        }

        mLock.unlock();

        mFrameRateLogger.incrementRenderCount();
    }

    @Override
    public void themeChanged(Theme theme) {
        mBackgroundColor = theme.getColor(R.attr.backgroundColor);
    }

    public boolean isPositionVisible(Vector2 position) {
        return mViewport.getScreenClipRect().contains(position.x(), position.y());
    }
}
