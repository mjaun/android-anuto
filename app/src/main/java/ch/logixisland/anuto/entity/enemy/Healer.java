package ch.logixisland.anuto.entity.enemy;

import java.util.ArrayList;
import java.util.Collection;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
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

    private final static String ENTITY_NAME = "healer";
    private final static float ANIMATION_SPEED = 1.5f;
    private final static float HEAL_SCALE_FACTOR = 2f;
    private final static float HEAL_ROTATION = 2.5f;

    private final static float HEAL_AMOUNT = 0.1f;
    private final static float HEAL_INTERVAL = 5.0f;
    private final static float HEAL_DURATION = 1.5f;
    private final static float HEAL_RADIUS = 0.7f;

    private final static EnemySettings ENEMY_SETTINGS = new EnemySettings(
            400,
            1.2f,
            0.012f,
            30,
            new WeaponType[]{WeaponType.Laser, WeaponType.Bullet},
            null,
            2.0f,
            0.5f
    );

    public static class Factory extends EntityFactory {
        @Override
        public String getEntityName() {
            return ENTITY_NAME;
        }

        @Override
        public Entity create(GameEngine gameEngine) {
            return new Healer(gameEngine);
        }
    }

    public static class Persister extends EnemyPersister {
        public Persister(GameEngine gameEngine, EntityRegistry entityRegistry) {
            super(gameEngine, entityRegistry, ENTITY_NAME);
        }
    }

    private static class StaticData implements TickListener {
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

                if (mScaleFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * HEAL_DURATION) {
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

    private StaticData mStaticData;

    private ReplicatedSprite mSprite;

    private Healer(GameEngine gameEngine) {
        super(gameEngine, ENEMY_SETTINGS);

        mStaticData = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createReplication(mStaticData.mReferenceSprite);
        mSprite.setListener(this);
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mHealTimer = TickTimer.createInterval(HEAL_INTERVAL);
        s.mHealedEnemies = new ArrayList<>();

        s.mScaleFunction = Function.sine()
                .join(Function.constant(0), (float) Math.PI)
                .multiply(HEAL_SCALE_FACTOR - 1f)
                .offset(1f)
                .stretch(GameEngine.TARGET_FRAME_RATE * HEAL_DURATION * 0.66f / (float) Math.PI)
                .invert()
                .sample();

        s.mRotateFunction = Function.constant(0)
                .join(Function.sine(), (float) Math.PI / 2f)
                .multiply(HEAL_ROTATION / GameEngine.TARGET_FRAME_RATE * 360f)
                .stretch(GameEngine.TARGET_FRAME_RATE * HEAL_DURATION * 0.66f / (float) Math.PI)
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
                    HEAL_AMOUNT,
                    HEAL_RADIUS,
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
