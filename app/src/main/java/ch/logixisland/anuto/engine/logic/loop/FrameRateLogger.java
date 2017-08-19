package ch.logixisland.anuto.engine.logic.loop;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import ch.logixisland.anuto.engine.render.Renderer;

public class FrameRateLogger {

    private static final String TAG = Renderer.class.getSimpleName();

    private static final int LOG_INTERVAL = 5000;

    private final Handler mDebugHandler = new Handler();
    private final AtomicInteger mLoopCount = new AtomicInteger();
    private final AtomicInteger mRenderCount = new AtomicInteger();

    public FrameRateLogger() {
        mDebugHandler.post(new Runnable() {
            @Override
            public void run() {
                int updateCount = mLoopCount.getAndSet(0) * 1000 / LOG_INTERVAL;
                int renderCount = mRenderCount.getAndSet(0) * 1000 / LOG_INTERVAL;

                Log.d(TAG, String.format("loop: %1$sHz; render: %2$sHz", updateCount, renderCount));
                mDebugHandler.postDelayed(this, LOG_INTERVAL);
            }
        });
    }

    public void incrementLoopCount() {
        mLoopCount.incrementAndGet();
    }

    public void incrementRenderCount() {
        mRenderCount.incrementAndGet();
    }
}
