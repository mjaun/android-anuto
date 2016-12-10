package ch.logixisland.anuto.game.render.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.render.Drawable;
import ch.logixisland.anuto.game.render.Layers;

public class RangeIndicator implements Drawable {

    private Tower mTower;
    private Paint mPen;

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
