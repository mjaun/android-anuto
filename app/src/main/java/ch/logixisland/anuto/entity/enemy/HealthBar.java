package ch.logixisland.anuto.entity.enemy;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.util.math.MathUtils;

public class HealthBar implements Drawable {
    private static final float HEALTHBAR_WIDTH = 1.0f;
    private static final float HEALTHBAR_HEIGHT = 0.1f;
    private static final float HEALTHBAR_OFFSET = 0.6f;

    private final Enemy mEntity;
    private static Paint mHealthBarBg = null;
    private static Paint mHealthBarFg = null;

    public HealthBar(Theme theme, Enemy entity) {
        mEntity = entity;

        if (mHealthBarBg == null) {
            mHealthBarBg = new Paint();
            mHealthBarBg.setColor(theme.getColor(R.attr.healthBarBackgroundColor));
        }
        if (mHealthBarFg == null) {
            mHealthBarFg = new Paint();
            mHealthBarFg.setColor(theme.getColor(R.attr.healthBarColor));
        }
    }

    @Override
    public int getLayer() {
        return Layers.ENEMY_HEALTHBAR;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!MathUtils.equals(mEntity.getHealth(), mEntity.getMaxHealth(), 1f)) {
            canvas.save();
            canvas.translate(mEntity.getPosition().x() - HEALTHBAR_WIDTH / 2f, mEntity.getPosition().y() + HEALTHBAR_OFFSET);

            canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
            canvas.drawRect(0, 0, mEntity.getHealth() / mEntity.getMaxHealth() * HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarFg);
            canvas.restore();
        }
    }
}
