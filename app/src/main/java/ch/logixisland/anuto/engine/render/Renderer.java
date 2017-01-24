package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.util.container.MultiMap;

public class Renderer {

    private final Viewport mViewport;
    private final ThemeManager mThemeManager;

    private MultiMap<Drawable> mDrawables = new MultiMap<>();
    private TripleBuffer mTripleBuffer = new TripleBuffer();
    private WeakReference<View> mViewRef;

    private volatile int mUpdateCount;
    private volatile int mDrawCount;

    public Renderer(Viewport viewport, ThemeManager themeManager) {
        mViewport = viewport;
        mThemeManager = themeManager;
    }

    public void setView(final View view) {
        mViewRef = new WeakReference<>(view);

        view.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Renderer", "UpdateCount = " + mUpdateCount);
                Log.d("Renderer", "DrawCount = " + mDrawCount);
                mUpdateCount = mDrawCount = 0;
                view.postDelayed(this, 1000);
            }
        });
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

    public void update() {
        View view = mViewRef.get();

        if (view != null) {
            view.postInvalidate();

            DrawCommandBuffer buffer = mTripleBuffer.getBufferForUpdating();

            for (Drawable drawable : mDrawables) {
                drawable.draw(buffer);
            }

            mTripleBuffer.updateFinished();
            mUpdateCount++;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(mThemeManager.getTheme().getBackgroundColor());
        canvas.concat(mViewport.getScreenMatrix());

        DrawCommandBuffer buffer = mTripleBuffer.getBufferForDrawing();

        for (DrawCommand command : buffer) {
            command.execute(canvas);
        }

        mTripleBuffer.drawFinished();
        mDrawCount++;
    }

}
