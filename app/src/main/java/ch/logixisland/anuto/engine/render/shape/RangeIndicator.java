package ch.logixisland.anuto.engine.render.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.entity.tower.Tower;

public class RangeIndicator implements Drawable {

    private final Tower mTower;
    private final Paint mPen;

    public RangeIndicator(ThemeManager themeManager, Tower tower) {
        mTower = tower;
        mPen = new Paint();
        mPen.setStyle(Paint.Style.STROKE);
        mPen.setStrokeWidth(0.05f);
        mPen.setColor(themeManager.getColor(R.attr.rangeIndicatorColor));
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
