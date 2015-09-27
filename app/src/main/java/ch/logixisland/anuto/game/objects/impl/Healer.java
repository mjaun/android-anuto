package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;

public class Healer extends Enemy {

    private final static float ANIMATION_SPEED = 1.5f;
    private final static float HEAL_SCALE_FACTOR = 2f;
    private final static float HEAL_ROTATION = 2.5f;

    private class StaticData extends GameEngine.StaticData {
        public float healDuration;
        public float healInterval;

        public boolean healing;
        public boolean dropEffect;
        public float angle;
        public float scale = 1f;
        public TickTimer healTimer;
        public SampledFunction scaleFunction;
        public SampledFunction rotateFunction;

        public Sprite sprite;
        public Sprite.AnimatedInstance animator;

        @Override
        public void tick() {
            animator.tick();

            if (healTimer.tick()) {
                healing = true;
            }

            if (healing) {
                rotateFunction.step();
                scaleFunction.step();

                angle += rotateFunction.getValue();
                scale = scaleFunction.getValue();

                if (scaleFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * healDuration) {
                    dropEffect = true;
                    healing = false;
                    angle = 0;
                    scale = 1f;

                    rotateFunction.reset();
                    scaleFunction.reset();
                }
            } else {
                dropEffect = false;
            }
        }
    }

    private float mHealAmount;
    private float mHealRange;
    private StaticData mStatic;

    private Sprite.Instance mSprite;

    public Healer() {
        mHealAmount = getProperty("healAmount");
        mHealRange = getProperty("healRadius");

        mStatic = (StaticData)getStaticData();

        mSprite = mStatic.animator.copycat();
        mSprite.setListener(this);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.healInterval = getProperty("healInterval");
        s.healDuration = getProperty("healDuration");

        s.healTimer = TickTimer.createInterval(s.healInterval);

        s.scaleFunction = Function.sine()
                .join(Function.zero(), (float) Math.PI)
                .multiply(HEAL_SCALE_FACTOR - 1f)
                .offset(1f)
                .stretch(GameEngine.TARGET_FRAME_RATE * s.healDuration * 0.66f / (float) Math.PI)
                .invert()
                .sample();

        s.rotateFunction = Function.zero()
                .join(Function.sine(), (float) Math.PI / 2f)
                .multiply(HEAL_ROTATION / GameEngine.TARGET_FRAME_RATE * 360f)
                .stretch(GameEngine.TARGET_FRAME_RATE * s.healDuration * 0.66f / (float) Math.PI)
                .sample();

        s.sprite = Sprite.fromResources(R.drawable.healer, 4);
        s.sprite.setMatrix(0.9f, 0.9f, null, null);

        s.animator = s.sprite.yieldAnimated(Layers.ENEMY);
        s.animator.setSequence(s.animator.sequenceForward());
        s.animator.setFrequency(ANIMATION_SPEED);

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

        canvas.rotate(mStatic.angle);
        canvas.scale(mStatic.scale, mStatic.scale);
    }

    @Override
    public void tick() {
        super.tick();

        if (mStatic.healing) {
            setBaseSpeed(0f);
        } else {
            setBaseSpeed(getConfigSpeed());
        }

        if (mStatic.dropEffect) {
            getGame().add(new HealEffect(this, getPosition(), mHealAmount, mHealRange));
        }
    }
}
