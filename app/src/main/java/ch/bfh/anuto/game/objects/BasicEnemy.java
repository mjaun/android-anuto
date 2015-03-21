package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.data.Path;
import ch.bfh.anuto.game.Sprite;

public class BasicEnemy extends Enemy {
    private final static int HEALTH = 100;
    private final static float MOVEMENT_SPEED = 1.5f / GameEngine.TARGET_FPS;
    private final static float ANIMATION_SPEED = 1f / GameEngine.TARGET_FPS;

    public BasicEnemy() {
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_enemy, 12);
        mSprite.getMatrix().postScale(0.9f, 0.9f);
    }

    @Override
    public void tick() {
        super.tick();

        mSprite.cycle2(ANIMATION_SPEED);
    }
}
