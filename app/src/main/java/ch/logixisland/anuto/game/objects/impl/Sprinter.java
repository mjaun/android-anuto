package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;

public class Sprinter extends Enemy {

    private final static float ANIMATION_SPEED = 0.7f;

    private static Sprite.Animator sAnimator;
    private static SampledFunction sSpeedFunction;

    private float mAngle;

    private final Sprite mSprite;

    public Sprinter() {
        mSprite = Sprite.fromResources(getGame().getResources(), R.drawable.sprinter, 6);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        if (sAnimator == null) {
            sAnimator = new Sprite.SynchronizedAnimator();
            sAnimator.setSequence(mSprite.sequenceForwardBackward());
            sAnimator.setFrequency(ANIMATION_SPEED);
        }

        mSprite.setAnimator(sAnimator);

        if (sSpeedFunction == null) {
            sSpeedFunction = Function.sine()
                    .multiply(0.9f)
                    .offset(0.1f)
                    .stretch(GameEngine.TARGET_FRAME_RATE / ANIMATION_SPEED / (float)Math.PI)
                    .sample();

            getGame().add(new Runnable() {
                @Override
                public void run() {
                    sSpeedFunction.step();
                }
            });
        }
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasWayPoint()) {
            mAngle = getDirection().angle();
            mBaseSpeed = sSpeedFunction.getValue();

            mSprite.animate();
            sSpeedFunction.step();
        }
    }
}
