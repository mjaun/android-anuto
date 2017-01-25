package ch.logixisland.anuto.engine.render;

import android.graphics.Canvas;

public interface Drawable {
    int getLayer();

    void draw(Canvas canvas);
}
