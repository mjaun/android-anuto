package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AreaEffect;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueEffect extends AreaEffect {

    private final static float EFFECT_DURATION = 2f;
    private final static float SPEED_MODIFIER = 0.5f;

    private final static int ALPHA_START = 150;
    private final static int ALPHA_STEP = (int)(ALPHA_START / GameEngine.TARGET_FRAME_RATE / EFFECT_DURATION);

    private float mAngle;

    private final List<Enemy> mAffected = new ArrayList<>();

    private final Paint mPaint;
    private final Sprite mSprite;

    public GlueEffect(Vector2 position) {
        setPosition(position);

        mDuration = EFFECT_DURATION;
        mAngle = mGame.getRandom(360f);

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.glue_effect, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom().nextInt(4));
        mSprite.setMatrix(1f, 1f, null, null);
        mSprite.setLayer(Layers.EFFECT_BOTTOM);

        mPaint = new Paint();
        mPaint.setAlpha(ALPHA_START);
        mSprite.setPaint(mPaint);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        mPaint.setAlpha(mPaint.getAlpha() - ALPHA_STEP);
    }

    @Override
    protected void enemyEnter(Enemy e) {
        e.modifySpeed(SPEED_MODIFIER);
    }

    @Override
    protected void enemyExit(Enemy e) {
        e.modifySpeed(1f / SPEED_MODIFIER);
    }
}
