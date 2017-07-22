package ch.logixisland.anuto.engine.render.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.util.math.Vector2;

public class LevelIndicator implements Drawable {

    private final EntityWithLevel mEntity;
    private final Paint mText;

    LevelIndicator(Theme theme, EntityWithLevel entity) {
        mEntity = entity;

        mText = new Paint();
        mText.setStyle(Paint.Style.FILL);
        mText.setColor(theme.getColor(R.attr.levelIndicatorColor));
        mText.setTextSize(100);
    }

    @Override
    public void draw(Canvas canvas) {
        Vector2 pos = mEntity.getPosition();

        canvas.save();
        canvas.translate(pos.x(), pos.y());
        canvas.scale(0.0075f, -0.0075f);
        String text = String.valueOf(mEntity.getLevel());
        float height = mText.ascent() + mText.descent();
        float width = mText.measureText(text);
        canvas.drawText(text, -width / 2, -height / 2, mText);
        canvas.restore();
    }

    @Override
    public int getLayer() {
        return Layers.TOWER_LEVEL;
    }
}
