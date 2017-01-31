package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.theme.ThemeListener;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.util.container.MultiMap;

public class Renderer implements ThemeListener {

    private final Viewport mViewport;
    private final ThemeManager mThemeManager;
    private final MultiMap<Drawable> mDrawables = new MultiMap<>();
    private final Lock mLock = new ReentrantLock(true);

    private int mBackgroundColor;
    private WeakReference<View> mViewRef;

    public Renderer(Viewport viewport, ThemeManager themeManager) {
        mViewport = viewport;
        mThemeManager = themeManager;
        mThemeManager.addListener(this);
        themeChanged();
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
