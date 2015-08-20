package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;

public class Healer extends Enemy {

    private final static float ANIMATION_SPEED = 1.5f;

    private final static float HEAL_AMOUNT = 200f;

    private final static float HEAL_INTERVAL = 5.0f;
    private final static float HEAL_DURATION = 1.5f;
    private final static float HEAL_SCALE_FACTOR = 2f;
    private final static float HEAL_ROTATION = 2.5f;

    private boolean mHealing;

    private SampledFunction mScaleFunction;
    private SampledFunction mRotateFunction;

    private Sprite.Animator mAnimator;
    private TickTimer mHealTimer;

    private float mAngle;
    private float mScale = 1f;

    private final Sprite mSprite;

    public Healer() {
        mHealTimer = TickTimer.createInterval(HEAL_INTERVAL);

        mScaleFunction = Function.sine()
                .join(Function.zero(), (float) Math.PI)
                .multiply(HEAL_SCALE_FACTOR - 1f)
                .offset(1f)
                .stretch(GameEngine.TARGET_FRAME_RATE * HEAL_DURATION * 0.66f / (float) Math.PI)
                .sample();

        mRotateFunction = Function.zero()
                .join(Function.sine(), (float) Math.PI / 2f)
                .multiply(HEAL_ROTATION / GameEngine.TARGET_FRAME_RATE * 360f)
                .stretch(GameEngine.TARGET_FRAME_RATE * HEAL_DURATION * 0.66f / (float) Math.PI)
                .sample();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.healer, 4);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        mAnimator = new Sprite.Animator();
        mAnimator.setSequence(mSprite.sequenceForward());
        mAnimator.setFrequency(ANIMATION_SPEED);

        mSprite.setAnimator(mAnimator);
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
        canvas.scale(mScale, mScale);
    }

    @Override
    public void tick() {
        super.tick();

        mSprite.animate();

        if (mHealTimer.tick()) {
            mHealing = true;
        }

        if (mHealing) {
            mRotateFunction.step();
            mScaleFunction.step();

            mBaseSpeed = 0f;
            mAngle += mRotateFunction.getValue();
            mScale = 1f / mScaleFunction.getValue();

            if (mScaleFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * HEAL_DURATION) {
                mHealing = false;
                mRotateFunction.reset();
                mScaleFunction.reset();

                mGame.add(new HealEffect(mPosition, HEAL_AMOUNT));
            }
        } else {
            mBaseSpeed = getConfigSpeed();
            mAngle = 0f;
            mScale = 1f;
        }
    }
}
