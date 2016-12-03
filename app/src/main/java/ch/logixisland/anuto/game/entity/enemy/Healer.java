package ch.logixisland.anuto.game.entity.enemy;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.effect.HealEffect;
import ch.logixisland.anuto.game.render.AnimatedSprite;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.render.ReplicatedSprite;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.util.math.function.Function;
import ch.logixisland.anuto.util.math.function.SampledFunction;

public class Healer extends Enemy {

    private final static float ANIMATION_SPEED = 1.5f;
    private final static float HEAL_SCALE_FACTOR = 2f;
    private final static float HEAL_ROTATION = 2.5f;

    private class StaticData implements Runnable {
        float mHealDuration;
        float mHealInterval;

        boolean mHealing;
        boolean mDropEffect;
        float mAngle;
        float mScale = 1f;
        TickTimer mHealTimer;
        SampledFunction mScaleFunction;
        SampledFunction mRotateFunction;

        SpriteTemplate mSpriteTemplate;
        AnimatedSprite mReferenceSprite;

        @Override
        public void run() {
            mReferenceSprite.tick();

            if (mHealTimer.tick()) {
                mHealing = true;
            }

            if (mHealing) {
                mRotateFunction.step();
                mScaleFunction.step();

                mAngle += mRotateFunction.getValue();
                mScale = mScaleFunction.getValue();

                if (mScaleFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * mHealDuration) {
                    mDropEffect = true;
                    mHealing = false;
                    mAngle = 0;
                    mScale = 1f;

                    mRotateFunction.reset();
                    mScaleFunction.reset();
                }
            } else {
                mDropEffect = false;
            }
        }
    }

    private float mHealAmount;
    private float mHealRange;
    private StaticData mStatic;

    private ReplicatedSprite mSprite;

    public Healer() {
        mHealAmount = getProperty("healAmount");
        mHealRange = getProperty("healRadius");

        mStatic = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createReplication(mStatic.mReferenceSprite);
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mHealInterval = getProperty("healInterval");
        s.mHealDuration = getProperty("healDuration");

        s.mHealTimer = TickTimer.createInterval(s.mHealInterval);

        s.mScaleFunction = Function.sine()
                .join(Function.zero(), (float) Math.PI)
                .multiply(HEAL_SCALE_FACTOR - 1f)
                .offset(1f)
                .stretch(GameEngine.TARGET_FRAME_RATE * s.mHealDuration * 0.66f / (float) Math.PI)
                .invert()
                .sample();

        s.mRotateFunction = Function.zero()
                .join(Function.sine(), (float) Math.PI / 2f)
                .multiply(HEAL_ROTATION / GameEngine.TARGET_FRAME_RATE * 360f)
                .stretch(GameEngine.TARGET_FRAME_RATE * s.mHealDuration * 0.66f / (float) Math.PI)
                .sample();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.healer, 4);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, null);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForward();
        s.mReferenceSprite.setFrequency(ANIMATION_SPEED);

        getGame().add(s);

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
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mStatic.mAngle);
        canvas.scale(mStatic.mScale, mStatic.mScale);
    }

    @Override
    public void tick() {
        super.tick();

        if (mStatic.mHealing) {
            setBaseSpeed(0f);
        } else {
            setBaseSpeed(getConfigSpeed());
        }

        if (mStatic.mDropEffect) {
            getGame().add(new HealEffect(this, getPosition(), mHealAmount, mHealRange));
        }
    }
}
