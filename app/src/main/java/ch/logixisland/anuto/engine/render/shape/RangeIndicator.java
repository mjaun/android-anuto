package ch.logixisland.anuto.engine.render.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.theme.Theme;

public class RangeIndicator implements Drawable {

    private final EntityWithRange mEntity;
    private final Paint mPen;

    public RangeIndicator(Theme theme, EntityWithRange entity) {
        mEntity = entity;
        mPen = new Paint();
        mPen.setStyle(Paint.Style.STROKE);
        mPen.setStrokeWidth(0.05f);
        mPen.setColor(theme.getColor(R.attr.rangeIndicatorColor));
    }

    @Override
    public int getLayer() {
        return Layers.TOWER_RANGE;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mEntity.getPosition().x(), mEntity.getPosition().y(), mEntity.getRange(), mPen);
    }

}
