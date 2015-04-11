package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Enemy;

public class EnemySprinter extends Enemy {

    private final static int HEALTH = 500;
    private final static float MOVEMENT_SPEED = 3f / GameEngine.TARGET_FPS;
    private final static float ANIMATION_SPEED = 1.5f / GameEngine.TARGET_FPS;

    private float mAngle;
    private Sprite mSprite;

    public EnemySprinter() {
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void init(Resources res) {
        super.init(res);

        mSprite = Sprite.fromResources(this, res, R.drawable.sprinter_enemy, 1);
        mSprite.calcMatrix(0.9f);
        mGame.addDrawObject(mSprite, LAYER);
    }

    @Override
    public void beforeDraw(Sprite sprite, Canvas canvas) {
        canvas.rotate(mAngle);
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
        mAngle = getDirectionTo(getWayPoint()).angle();
    }
}
