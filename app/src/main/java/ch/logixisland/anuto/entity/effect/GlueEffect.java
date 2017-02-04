package ch.logixisland.anuto.entity.effect;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.Flyer;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GlueEffect extends AreaEffect {

    private final static int ALPHA_START = 150;

    private class StaticData {
        SpriteTemplate mSpriteTemplate;
    }

    private float mAngle;
    private float mIntensity;
    private int mAlphaStep;

    private Paint mPaint;
    private StaticSprite mSprite;

    public GlueEffect(Entity origin, Vector2 position, float intensity, float duration) {
        super(origin, duration, 1f);
        setPosition(position);

        mIntensity = intensity;
        mAngle = RandomUtils.next(360f);
        mAlphaStep = (int) (ALPHA_START / (GameEngine.TARGET_FRAME_RATE * duration));

        StaticData s = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createStatic(Layers.BOTTOM, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setIndex(RandomUtils.next(4));

        mPaint = new Paint();
        mPaint.setAlpha(ALPHA_START);
        mSprite.setPaint(mPaint);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.glueEffect, 4);
        s.mSpriteTemplate.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        mPaint.setAlpha(mPaint.getAlpha() - mAlphaStep);
    }

    @Override
    protected void enemyEnter(Enemy e) {
        if (!(e instanceof Flyer)) {
            e.modifySpeed(1f / mIntensity);
        }
    }

    @Override
    protected void enemyExit(Enemy e) {
        if (!(e instanceof Flyer)) {
            e.modifySpeed(mIntensity);
        }
    }
}
