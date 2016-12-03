package ch.logixisland.anuto.game.entity.effect;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.entity.enemy.Flyer;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.Random;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GlueEffect extends AreaEffect {

    private final static int ALPHA_START = 150;

    private class StaticData {
        SpriteTemplate mSpriteTemplate;
    }

    private float mAngle;
    private float mSpeedModifier;
    private int mAlphaStep;

    private Paint mPaint;
    private StaticSprite mSprite;

    public GlueEffect(Entity origin, Vector2 position, float speedModifier, float duration) {
        super(origin, duration);
        setPosition(position);

        mSpeedModifier = speedModifier;
        mAngle = Random.next(360f);
        mAlphaStep = (int)(ALPHA_START / (GameEngine.TARGET_FRAME_RATE * duration));

        StaticData s = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createStatic(Layers.BOTTOM, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setIndex(Random.next(4));

        mPaint = new Paint();
        mPaint.setAlpha(ALPHA_START);
        mSprite.setPaint(mPaint);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.glue_effect, 4);
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
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

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
            e.modifySpeed(mSpeedModifier);
        }
    }

    @Override
    protected void enemyExit(Enemy e) {
        if (!(e instanceof Flyer)) {
            e.modifySpeed(1f / mSpeedModifier);
        }
    }
}
