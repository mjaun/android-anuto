package ch.logixisland.anuto.engine.logic.loop;

import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class FrameRateLogger {

    private static final String TAG = FrameRateLogger.class.getSimpleName();

    private static final int LOG_INTERVAL = 5000;

    private final AtomicInteger mLoopCount = new AtomicInteger();
    private final AtomicInteger mRenderCount = new AtomicInteger();

    private long mLastOutputTime;

    public void incrementLoopCount() {
        mLoopCount.incrementAndGet();
    }

    public void incrementRenderCount() {
        mRenderCount.incrementAndGet();
    }

    public void outputFrameRate() {
        long currentTime = System.currentTimeMillis();
        long sinceLastOutput = currentTime - mLastOutputTime;

        if (sinceLastOutput >= LOG_INTERVAL) {
            long loopCount = mLoopCount.getAndSet(0);
            long renderCount = mRenderCount.getAndSet(0);

            loopCount = loopCount * 1000 / sinceLastOutput;
            renderCount = renderCount * 1000 / sinceLastOutput;
            Log.d(TAG, String.format("loop: %1$sHz; render: %2$sHz", loopCount, renderCount));

            mLastOutputTime = currentTime;
        }
    }
}
