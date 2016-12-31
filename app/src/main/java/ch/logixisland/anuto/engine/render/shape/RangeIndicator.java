package ch.logixisland.anuto.engine.render.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;

public class RangeIndicator implements Drawable {

    private final Tower mTower;
    private final Paint mPen;

    public RangeIndicator(Tower tower) {
        mTower = tower;
        mPen = new Paint();
        mPen.setStyle(Paint.Style.STROKE);
        mPen.setStrokeWidth(0.05f);
        mPen.setColor(Color.GREEN);
        mPen.setAlpha(128);
    }

    @Override
    public int getLayer() {
        return Layers.TOWER_RANGE;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mTower.getPosition().x, mTower.getPosition().y, mTower.getRange(), mPen);
    }

}
