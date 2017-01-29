package ch.logixisland.anuto.engine.render.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.math.MathUtils;

public class HealthBar implements Drawable {
    private static final float HEALTHBAR_WIDTH = 1.0f;
    private static final float HEALTHBAR_HEIGHT = 0.1f;
    private static final float HEALTHBAR_OFFSET = 0.6f;

    private final Enemy mEnemy;
    private final Paint mHealthBarBg;
    private final Paint mHealthBarFg;

    public HealthBar(ThemeManager themeManager, Enemy enemy) {
        mEnemy = enemy;

        mHealthBarBg = new Paint();
        mHealthBarBg.setColor(themeManager.getColor(R.attr.healthBarBackgroundColor));
        mHealthBarFg = new Paint();
        mHealthBarFg.setColor(themeManager.getColor(R.attr.healthBarColor));
    }

    @Override
    public int getLayer() {
        return Layers.ENEMY_HEALTHBAR;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!MathUtils.equals(mEnemy.getHealth(), mEnemy.getHealthMax(), 1f)) {
            canvas.save();
            canvas.translate(mEnemy.getPosition().x - HEALTHBAR_WIDTH / 2f, mEnemy.getPosition().y + HEALTHBAR_OFFSET);

            canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
            canvas.drawRect(0, 0, mEnemy.getHealth() / mEnemy.getHealthMax() * HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarFg);
            canvas.restore();
        }
    }
}
