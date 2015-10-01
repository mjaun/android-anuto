package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Paint;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AreaEffect;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueEffect extends AreaEffect {

    private final static int ALPHA_START = 150;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
    }

    private float mAngle;
    private float mSpeedModifier;
    private int mAlphaStep;

    private Paint mPaint;
    private Sprite.FixedInstance mSprite;

    public GlueEffect(GameObject origin, Vector2 position, float speedModifier, float duration) {
        super(origin, duration);
        setPosition(position);

        mSpeedModifier = speedModifier;
        mAngle = getGame().getRandom(360f);
        mAlphaStep = (int)(ALPHA_START / (GameEngine.TARGET_FRAME_RATE * duration));

        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldStatic(Layers.BOTTOM);
        mSprite.setListener(this);
        mSprite.setIndex(getGame().getRandom().nextInt(4));

        mPaint = new Paint();
        mPaint.setAlpha(ALPHA_START);
        mSprite.setPaint(mPaint);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.glue_effect, 4);
        s.sprite.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
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
