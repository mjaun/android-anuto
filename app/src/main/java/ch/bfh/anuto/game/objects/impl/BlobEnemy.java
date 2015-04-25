package ch.bfh.anuto.game.objects.impl;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Sprite;

public class BlobEnemy extends Enemy {

    private final static int REWARD = 20;
    private final static int HEALTH = 2000;
    private final static float MOVEMENT_SPEED = 1f;
    private final static float ANIMATION_SPEED = 1.5f;

    private Sprite mSprite;

    public BlobEnemy() {
        mReward = REWARD;
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.blob_enemy, 9);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f);
        mSprite.setLayer(Layers.ENEMY);
        mSprite.getAnimator().setSequence(mSprite.sequenceForward());
        mSprite.getAnimator().setSpeed(ANIMATION_SPEED);
        mGame.add(mSprite);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onTick() {
        super.onTick();

        mSprite.animate();
    }
}
