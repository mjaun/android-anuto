package ch.bfh.anuto.game;

import android.graphics.PointF;

import org.simpleframework.xml.Root;

@Root
public abstract class Plateau extends GameObject {
    public static final int LAYER = 1;

    @Override
    public int getLayer() {
        return LAYER;
    }
}
