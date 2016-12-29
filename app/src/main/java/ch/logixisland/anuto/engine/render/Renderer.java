package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;
import android.view.View;

import java.lang.ref.WeakReference;

import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.util.container.SparseCollectionArray;

public class Renderer {

    private final Viewport mViewport;
    private final ThemeManager mThemeManager;
    private final SparseCollectionArray<Drawable> mDrawables = new SparseCollectionArray<>();
    private WeakReference<View> mViewRef;

    private volatile boolean mAllowedToDraw = false;

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

    public synchronized void render() throws InterruptedException {
        View view = mViewRef.get();

        if (view != null) {
            mAllowedToDraw = true;
            view.postInvalidate();
            wait();
        }
    }

    public synchronized void draw(Canvas canvas) {
        if (!mAllowedToDraw) {
            return;
        }

        canvas.drawColor(mThemeManager.getTheme().getBackgroundColor());
        canvas.concat(mViewport.getScreenMatrix());

        for (Drawable obj : mDrawables) {
            obj.draw(canvas);
        }

        mAllowedToDraw = false;
        notifyAll();
    }

}
