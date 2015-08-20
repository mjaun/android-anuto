package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;

public class Healer extends Enemy {

    private final static float ANIMATION_SPEED = 1.5f;
    private final static float HEAL_INTERVAL = 5.0f;

    private boolean mHealing;

    private float mAngle;
    private float mScale = 1f;

    private final TickTimer mHealTimer;

    private final Sprite mSprite;

    public Healer() {
        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.healer, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom(4));
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSprite.sequenceForward());
        animator.setFrequency(ANIMATION_SPEED);
        mSprite.setAnimator(animator);

        mHealTimer = TickTimer.createInterval(HEAL_INTERVAL);
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
            mBaseSpeed = 0f;

        } else {
            mBaseSpeed = getConfigSpeed();
            mAngle = 0f;
            mScale = 1f;
        }
    }
}
