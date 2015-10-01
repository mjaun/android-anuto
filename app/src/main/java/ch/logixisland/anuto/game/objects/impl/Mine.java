package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.iterator.Predicate;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;
import ch.logixisland.anuto.util.math.Vector2;

public class Mine extends Shot {

    private final static float TRIGGER_RADIUS = 0.7f;

    private final static float TIME_TO_TARGET = 1.5f;
    private final static float ROTATION_RATE_MIN = 0.5f;
    private final static float ROTATION_RATE_MAX = 2.0f;
    private final static float HEIGHT_SCALING_START = 0.5f;
    private final static float HEIGHT_SCALING_STOP = 1.0f;
    private final static float HEIGHT_SCALING_PEAK = 1.5f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
    }

    private float mDamage;
    private float mRadius;
    private float mAngle;
    private boolean mFlying = true;
    private float mRotationStep;
    private SampledFunction mHeightScalingFunction;

    private Sprite.FixedInstance mSpriteFlying;
    private Sprite.FixedInstance mSpriteMine;

    public Mine(GameObject origin, Vector2 position, Vector2 target, float damage, float radius) {
        super(origin);
        setPosition(position);

        setSpeed(getDistanceTo(target) / TIME_TO_TARGET);
        setDirection(getDirectionTo(target));

        mDamage = damage;
        mRadius = radius;

        mRotationStep = getGame().getRandom(ROTATION_RATE_MIN, ROTATION_RATE_MAX) * 360f / GameEngine.TARGET_FRAME_RATE;

        StaticData s = (StaticData)getStaticData();

        float x1 = (float)Math.sqrt(HEIGHT_SCALING_PEAK - HEIGHT_SCALING_START);
        float x2 = (float)Math.sqrt(HEIGHT_SCALING_PEAK - HEIGHT_SCALING_STOP);
        mHeightScalingFunction = Function.quadratic()
                .multiply(-1f)
                .offset(HEIGHT_SCALING_PEAK)
                .shift(-x1)
                .stretch(GameEngine.TARGET_FRAME_RATE * TIME_TO_TARGET / (x1 + x2))
                .sample();

        int index = getGame().getRandom(4);

        mSpriteFlying = s.sprite.yieldStatic(Layers.SHOT);
        mSpriteFlying.setListener(this);
        mSpriteFlying.setIndex(index);

        mSpriteMine = s.sprite.yieldStatic(Layers.BOTTOM);
        mSpriteMine.setListener(this);
        mSpriteMine.setIndex(index);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.mine, 4);
        s.sprite.setMatrix(0.7f, 0.7f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        if (mFlying) {
            getGame().add(mSpriteFlying);
        } else {
            getGame().add(mSpriteMine);
        }
    }

    @Override
    public void clean() {
        super.clean();

        if (mFlying) {
            getGame().remove(mSpriteFlying);
        } else {
            getGame().remove(mSpriteMine);
        }
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

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
                getGame().remove(mSpriteFlying);
                getGame().add(mSpriteMine);

                mFlying = false;
                setSpeed(0f);
            }
        } else if (getGame().tick100ms(this)) {
            StreamIterator<Enemy> enemiesInRange = getGame().get(Enemy.TYPE_ID)
                    .filter(inRange(getPosition(), TRIGGER_RADIUS))
                    .cast(Enemy.class)
                    .filter(new Predicate<Enemy>() {
                        @Override
                        public boolean apply(Enemy value) {
                            return !(value instanceof Flyer);
                        }
                    });

            if (!enemiesInRange.isEmpty()) {
                getGame().add(new Explosion(getOrigin(), getPosition(), mDamage, mRadius));
                this.remove();
            }
        }
    }
}
