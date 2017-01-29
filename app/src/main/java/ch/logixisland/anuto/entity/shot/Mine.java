package ch.logixisland.anuto.entity.shot;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.effect.Explosion;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.Flyer;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.function.Function;
import ch.logixisland.anuto.util.math.function.SampledFunction;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class Mine extends Shot {

    private final static float TRIGGER_RADIUS = 0.7f;

    private final static float TIME_TO_TARGET = 1.5f;
    private final static float ROTATION_RATE_MIN = 0.5f;
    private final static float ROTATION_RATE_MAX = 2.0f;
    private final static float HEIGHT_SCALING_START = 0.5f;
    private final static float HEIGHT_SCALING_STOP = 1.0f;
    private final static float HEIGHT_SCALING_PEAK = 1.5f;

    private class StaticData {
        SpriteTemplate mSpriteTemplate;
    }

    private float mDamage;
    private float mRadius;
    private float mAngle;
    private boolean mFlying = true;
    private float mRotationStep;
    private SampledFunction mHeightScalingFunction;

    private StaticSprite mSpriteFlying;
    private StaticSprite mSpriteMine;

    private final TickTimer mUpdateTimer = TickTimer.createInterval(0.1f);

    public Mine(Entity origin, Vector2 position, Vector2 target, float damage, float radius) {
        super(origin);
        setPosition(position);

        setSpeed(getDistanceTo(target) / TIME_TO_TARGET);
        setDirection(getDirectionTo(target));

        mDamage = damage;
        mRadius = radius;

        mRotationStep = RandomUtils.next(ROTATION_RATE_MIN, ROTATION_RATE_MAX) * 360f / GameEngine.TARGET_FRAME_RATE;

        StaticData s = (StaticData) getStaticData();

        float x1 = (float) Math.sqrt(HEIGHT_SCALING_PEAK - HEIGHT_SCALING_START);
        float x2 = (float) Math.sqrt(HEIGHT_SCALING_PEAK - HEIGHT_SCALING_STOP);
        mHeightScalingFunction = Function.quadratic()
                .multiply(-1f)
                .offset(HEIGHT_SCALING_PEAK)
                .shift(-x1)
                .stretch(GameEngine.TARGET_FRAME_RATE * TIME_TO_TARGET / (x1 + x2))
                .sample();

        int index = RandomUtils.next(4);

        mSpriteFlying = getSpriteFactory().createStatic(Layers.SHOT, s.mSpriteTemplate);
        mSpriteFlying.setListener(this);
        mSpriteFlying.setIndex(index);

        mSpriteMine = getSpriteFactory().createStatic(Layers.BOTTOM, s.mSpriteTemplate);
        mSpriteMine.setListener(this);
        mSpriteMine.setIndex(index);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.mine, 4);
        s.mSpriteTemplate.setMatrix(0.7f, 0.7f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        if (mFlying) {
            getGameEngine().add(mSpriteFlying);
        } else {
            getGameEngine().add(mSpriteMine);
        }
    }

    @Override
    public void clean() {
        super.clean();

        if (mFlying) {
            getGameEngine().remove(mSpriteFlying);
        } else {
            getGameEngine().remove(mSpriteMine);
        }
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        float s = mHeightScalingFunction.getValue();
        canvas.scale(s, s);
        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mFlying) {
            mAngle += mRotationStep;
            mHeightScalingFunction.step();

            if (mHeightScalingFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * TIME_TO_TARGET) {
                getGameEngine().remove(mSpriteFlying);
                getGameEngine().add(mSpriteMine);

                mFlying = false;
                setSpeed(0f);
            }
        } else if (mUpdateTimer.tick()) {
            StreamIterator<Enemy> enemiesInRange = getGameEngine().get(Types.ENEMY)
                    .filter(inRange(getPosition(), TRIGGER_RADIUS))
                    .cast(Enemy.class)
                    .filter(new Predicate<Enemy>() {
                        @Override
                        public boolean apply(Enemy value) {
                            return !(value instanceof Flyer);
                        }
                    });

            if (!enemiesInRange.isEmpty()) {
                getGameEngine().add(new Explosion(getOrigin(), getPosition(), mDamage, mRadius));
                this.remove();
            }
        }
    }
}
