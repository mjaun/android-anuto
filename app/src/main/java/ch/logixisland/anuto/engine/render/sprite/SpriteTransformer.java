package ch.logixisland.anuto.engine.render.sprite;

import android.graphics.Canvas;

import ch.logixisland.anuto.util.math.Vector2;

public class SpriteTransformer {
    public static void translate(Canvas canvas, Vector2 position) {
        translate(canvas, position.x(), position.y());
    }

    public static void translate(Canvas canvas, float x, float y) {
        canvas.translate(x, y);
    }

    public static void rotate(Canvas canvas, float angle) {
        canvas.rotate(angle);
    }

    public static void scale(Canvas canvas, float s) {
        canvas.scale(s, s);
    }
}
