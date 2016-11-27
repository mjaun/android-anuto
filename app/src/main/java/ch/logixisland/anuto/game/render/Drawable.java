package ch.logixisland.anuto.game.render;

import android.graphics.Canvas;

public interface Drawable {
    int getLayer();
    void draw(Canvas canvas);
}
