package ch.logixisland.anuto.engine.render.sprite;

import android.graphics.Canvas;

import ch.logixisland.anuto.engine.logic.Entity;
import ch.logixisland.anuto.util.math.Vector2;

public class SpriteTransformer {
    private final Canvas mCanvas;

    public SpriteTransformer(Canvas canvas) {
        mCanvas = canvas;
    }

    public void translate(Entity entity) {
        translate(entity.getPosition());
    }

    public void translate(Vector2 position) {
        translate(position.x(), position.y());
    }

    public void translate(float x, float y) {
        mCanvas.translate(x, y);
    }

    public void rotate(float angle) {
        mCanvas.rotate(angle);
    }

    public void scale(float s) {
        mCanvas.scale(s, s);
    }
}
