package ch.bfh.anuto.game.objects.impl;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Sprite;

public class Blob extends Enemy {

    private final static int REWARD = 20;
    private final static int HEALTH = 2000;
    private final static float MOVEMENT_SPEED = 1f;
    private final static float ANIMATION_SPEED = 1.5f;

    private final Sprite mSprite;

    public Blob() {
        mReward = REWARD;
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.blob, 9);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSprite.sequenceForward());
        animator.setSpeed(ANIMATION_SPEED);
        mSprite.setAnimator(animator);
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
    public void tick() {
        super.tick();

        mSprite.animate();
    }
}
