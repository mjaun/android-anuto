package ch.bfh.anuto.game;

import android.graphics.Canvas;

public abstract class DrawObject {
    private int mLayer = 0;

    public abstract void draw(Canvas canvas);

    public int getLayer() {
        return mLayer;
    }

    public void setLayer(int layer) {
        mLayer = layer;
    }
}
