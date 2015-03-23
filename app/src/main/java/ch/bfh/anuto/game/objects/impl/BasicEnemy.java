package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Enemy;

public class BasicEnemy extends Enemy {

    private final static int HEALTH = 1000;
    private final static float MOVEMENT_SPEED = 2.0f / GameEngine.TARGET_FPS;
    private final static float ANIMATION_SPEED = 1.5f / GameEngine.TARGET_FPS;

    private Sprite mSprite;

    public BasicEnemy() {
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void init(Resources res) {
        super.init(res);

        mSprite = Sprite.fromResources(this, res, R.drawable.basic_enemy, 12);
        mSprite.calcMatrix(0.9f);
        mGame.addDrawObject(mSprite, LAYER);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.removeDrawObject(mSprite);
    }

    @Override
    public void tick() {
        super.tick();
        mSprite.cycle2(ANIMATION_SPEED);
    }
}
