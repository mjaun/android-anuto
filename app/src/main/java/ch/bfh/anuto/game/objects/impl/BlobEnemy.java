package ch.bfh.anuto.game.objects.impl;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.TickTimer;

public class BlobEnemy extends Enemy {

    private final static int REWARD = 20;
    private final static int HEALTH = 2000;
    private final static float MOVEMENT_SPEED = 1f;
    private final static float ANIMATION_SPEED = 1.5f;

    private Sprite mSprite;
    private TickTimer mSpriteTimer;

    public BlobEnemy() {
        mReward = REWARD;
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(this, R.drawable.blob_enemy, 9);
        mSprite.calcMatrix(0.9f);
        mSprite.setLayer(Layers.ENEMY);
        mGame.add(mSprite);

        mSpriteTimer = TickTimer.createFrequency(ANIMATION_SPEED * (mSprite.count() * 2 - 1));
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mSpriteTimer.tick()) {
            mSprite.cycle2();
        }
    }
}
