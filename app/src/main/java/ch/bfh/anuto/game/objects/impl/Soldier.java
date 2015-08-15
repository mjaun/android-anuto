package ch.bfh.anuto.game.objects.impl;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Sprite;

public class Soldier extends Enemy {

    private final static int REWARD = 10;
    private final static int HEALTH = 1000;
    private final static float MOVEMENT_SPEED = 2f;
    private final static float ANIMATION_SPEED = 1.5f;

    private static Sprite.Animator sSpriteAnimator;

    private Sprite mSprite;

    public Soldier() {
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
        mReward = REWARD;
    }

    @Override
    public void init() {
        super.init();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.soldier, 12);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        if (sSpriteAnimator == null) {
            sSpriteAnimator = new Sprite.Animator();
            sSpriteAnimator.setSequence(mSprite.sequenceForwardBackward());
            sSpriteAnimator.setSpeed(ANIMATION_SPEED);
        }

        mSprite.setAnimator(sSpriteAnimator);
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
