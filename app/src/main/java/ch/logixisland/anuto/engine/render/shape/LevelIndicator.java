package ch.logixisland.anuto.engine.render.shape;

import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.engine.render.DrawCommandBuffer;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class LevelIndicator implements Drawable {

    private final Tower mTower;
    private final Paint mPaint;

    LevelIndicator(Tower tower) {
        mTower = tower;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(100);
    }

    @Override
    public void draw(DrawCommandBuffer buffer) {
        Vector2 pos = mTower.getPosition();

        buffer.save();
        buffer.translate(pos.x, pos.y);
        buffer.scale(0.0075f, -0.0075f);
        String text = String.valueOf(mTower.getTowerLevel());
        float height = mPaint.ascent() + mPaint.descent();
        float width = mPaint.measureText(text);
        buffer.drawText(text, -width / 2, -height / 2, mPaint);
        buffer.restore();
    }

    @Override
    public int getLayer() {
        return Layers.TOWER_LEVEL;
    }
}
