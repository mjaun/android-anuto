package ch.bfh.anuto.game;

import android.graphics.Canvas;

import ch.bfh.anuto.util.RemovedMark;

public abstract class DrawObject implements RemovedMark {
    private boolean mRemovedMark = true;
    private int mLayer = 0;

    public abstract void draw(Canvas canvas);

    public int getLayer() {
        return mLayer;
    }

    public void setLayer(int layer) {
        if (mRemovedMark == true) {
            throw new UnsupportedOperationException();
        }

        mLayer = layer;
    }

    @Override
    public void resetRemovedMark() {
        mRemovedMark = false;
    }

    @Override
    public void markAsRemoved() {
        mRemovedMark = true;
    }

    @Override
    public boolean hasRemovedMark() {
        return mRemovedMark;
    }
}
