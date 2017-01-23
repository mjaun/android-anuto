package ch.logixisland.anuto.engine.render;

public class TripleBuffer {

    private DrawCommandBuffer[] mBuffers = new DrawCommandBuffer[3];

    private int mLockedForUpdatingIndex = -1;
    private int mLockedForDrawingIndex = -1;
    private int mLastUpdatedIndex = 0;

    public TripleBuffer() {
        mBuffers[0] = new DrawCommandBuffer();
        mBuffers[1] = new DrawCommandBuffer();
        mBuffers[2] = new DrawCommandBuffer();
    }

    public synchronized DrawCommandBuffer getBufferForDrawing() {
        mLockedForDrawingIndex = mLastUpdatedIndex;
        return mBuffers[mLockedForDrawingIndex];
    }

    public synchronized void drawFinished() {
        mLockedForDrawingIndex = -1;
    }

    public synchronized DrawCommandBuffer getBufferForUpdating() {
        if (mLockedForDrawingIndex != 0 && mLastUpdatedIndex != 0) {
            mLockedForUpdatingIndex = 0;
        } else if (mLockedForDrawingIndex != 1 && mLastUpdatedIndex != 1) {
            mLockedForUpdatingIndex = 1;
        } else {
            mLockedForUpdatingIndex = 2;
        }


        mBuffers[mLockedForUpdatingIndex].clear();
        return mBuffers[mLockedForUpdatingIndex];
    }

    public synchronized void updateFinished() {
        mLastUpdatedIndex = mLockedForUpdatingIndex;
        mLockedForUpdatingIndex = -1;
    }

}
