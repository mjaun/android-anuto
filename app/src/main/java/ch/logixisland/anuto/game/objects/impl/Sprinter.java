package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;

public class Sprinter extends Enemy {

    private final static float ANIMATION_SPEED = 1.0f;

    private static Sprite.Animator sAnimator;

    private float mAngle;

    private final Sprite mSprite;

    public Sprinter() {
        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.sprinter, 8);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        if (sAnimator == null) {
            sAnimator = new Sprite.Animator();
            sAnimator.setSequence(mSprite.sequenceForwardBackward());
            sAnimator.setFrequency(ANIMATION_SPEED);
        }

        mSprite.setAnimator(sAnimator);
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
            mAngle = getDirectionTo(getWayPoint()).angle();
            mSprite.animate();
            mSpeed = Math.abs(sAnimator.count() - sAnimator.getPosition() * 2) * mConfig.speed / sAnimator.count();
        }
    }
}
