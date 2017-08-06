package ch.logixisland.anuto.entity.enemy;

import java.util.ArrayList;
import java.util.Collection;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.TickListener;
import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.ReplicatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;
import ch.logixisland.anuto.entity.effect.HealEffect;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;

public class Healer extends Enemy implements SpriteTransformation {

    private final static float ANIMATION_SPEED = 1.5f;
    private final static float HEAL_SCALE_FACTOR = 2f;
    private final static float HEAL_ROTATION = 2.5f;

    private class StaticData implements TickListener {
        float mHealDuration;
        float mHealInterval;

        boolean mHealing;
        boolean mDropEffect;
        float mAngle;
        float mScale = 1f;
        TickTimer mHealTimer;
        Collection<Enemy> mHealedEnemies;
        SampledFunction mScaleFunction;
        SampledFunction mRotateFunction;

        SpriteTemplate mSpriteTemplate;
        AnimatedSprite mReferenceSprite;

        @Override
        public void tick() {
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
                    mHealedEnemies.clear();
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

    private HealerProperties mProperties;
    private StaticData mStaticData;

    private ReplicatedSprite mSprite;

    public Healer(GameEngine gameEngine, HealerProperties properties) {
        super(gameEngine, properties);

        mProperties = properties;
        mStaticData = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createReplication(mStaticData.mReferenceSprite);
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mHealInterval = mProperties.getHealInterval();
        s.mHealDuration = mProperties.getHealDuration();

        s.mHealTimer = TickTimer.createInterval(s.mHealInterval);
        s.mHealedEnemies = new ArrayList<>();

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

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.healer, 4);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, null);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForward();
        s.mReferenceSprite.setFrequency(ANIMATION_SPEED);

        getGameEngine().add(s);

        return s;
    }

    @Override
    public float getSpeed() {
        if (mStaticData.mHealing) {
            return 0f;
        } else {
            return super.getSpeed();
        }
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
    public void tick() {
        super.tick();

        if (mStaticData.mDropEffect) {
            getGameEngine().add(new HealEffect(this, getPosition(),
                    mProperties.getHealAmount(),
                    mProperties.getHealRadius(),
                    mStaticData.mHealedEnemies));
        }
    }

    @Override
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());
        transformer.rotate(mStaticData.mAngle);
        transformer.scale(mStaticData.mScale);
    }
}
