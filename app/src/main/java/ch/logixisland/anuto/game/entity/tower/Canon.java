package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.entity.shot.CanonShot;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.entity.shot.Shot;
import ch.logixisland.anuto.game.render.sprite.SpriteInstance;
import ch.logixisland.anuto.game.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.game.render.sprite.StaticSprite;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.function.Function;
import ch.logixisland.anuto.util.math.function.SampledFunction;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class Canon extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;
    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private float mAngle = 90f;
    private boolean mReboundActive;

    private SampledFunction mReboundFunction;

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteCanon;

    public Canon() {
        StaticData s = (StaticData)getStaticData();

        mReboundFunction = Function.sine()
                .multiply(REBOUND_RANGE)
                .stretch(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION / (float)Math.PI)
                .sample();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(RandomUtils.next(4));

        mSpriteCanon = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setIndex(RandomUtils.next(4));
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.drawable.base1, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.drawable.canon, 4);
        s.mSpriteTemplateCanon.setMatrix(0.4f, 1.0f, new Vector2(0.2f, 0.2f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSpriteBase);
        getGameEngine().add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSpriteBase);
        getGameEngine().remove(mSpriteCanon);
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        canvas.rotate(mAngle);

        if (sprite == mSpriteCanon && mReboundActive) {
            canvas.translate(-mReboundFunction.getValue(), 0);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Shot shot = new CanonShot(this, getPosition(), getTarget(), getDamage());
                shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                getGameEngine().add(shot);

                setReloaded(false);
                mReboundActive = true;
            }
        }

        if (mReboundActive) {
            mReboundFunction.step();
            if (mReboundFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION) {
                mReboundFunction.reset();
                mReboundActive = false;
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
