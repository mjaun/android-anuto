package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SineFunction;

public class Sprinter extends Enemy {

    private final static float ANIMATION_SPEED = 0.7f;

    private static Sprite.Animator sAnimator;
    private static Function sSpeedFunction;

    private float mAngle;

    private final Sprite mSprite;

    public Sprinter() {
        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.sprinter, 6);
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
            SineFunction f = new SineFunction();
            f.setProperties(0f, (float) Math.PI, mBaseSpeed * 0.9f, mBaseSpeed * 0.1f);
            f.setSection(GameEngine.TARGET_FRAME_RATE / ANIMATION_SPEED);
            sSpeedFunction = mGame.synchronize(f);
        }
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);
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
