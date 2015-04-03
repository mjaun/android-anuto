package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.Enemy;

public class BasicEnemy extends Enemy {

    private final static int HEALTH = 1000;
    private final static float MOVEMENT_SPEED = 2.0f;
    private final static float ANIMATION_SPEED = 1.5f;

    private Sprite mSprite;
    private TickTimer mSpriteTimer;

    public BasicEnemy() {
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void init(Resources res) {
        super.init(res);

        mSprite = Sprite.fromResources(this, res, R.drawable.basic_enemy, 12);
        mSprite.calcMatrix(0.9f);
        mSprite.setLayer(Layers.ENEMY);
        mGame.add(mSprite);

        mSpriteTimer = TickTimer.createFrequency(ANIMATION_SPEED * (mSprite.getCount() * 2 - 1));
    }

    @Override
    public void clean() {
        super.clean();
        mGame.remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        if (mSpriteTimer.tick()) {
            mSprite.cycle2();
        }
    }
}
