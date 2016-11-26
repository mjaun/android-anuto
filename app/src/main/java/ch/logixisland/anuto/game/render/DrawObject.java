package ch.logixisland.anuto.game.render;

import android.graphics.Canvas;

public abstract class DrawObject {
    public abstract int getLayer();

    public abstract void draw(Canvas canvas);
}
