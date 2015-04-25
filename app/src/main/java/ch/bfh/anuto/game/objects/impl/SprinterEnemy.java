package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Sprite;

public class SprinterEnemy extends Enemy {

    private final static int REWARD = 5;
    private final static int HEALTH = 500;
    private final static float MOVEMENT_SPEED = 3.0f;

    private float mAngle;
    private Sprite mSprite;

    public SprinterEnemy() {
        mReward = REWARD;
        mHealth = mHealthMax = HEALTH;
        mSpeed = MOVEMENT_SPEED;
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.sprinter_enemy);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f);
        mSprite.setLayer(Layers.ENEMY);
        mGame.add(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (hasWayPoint()) {
            mAngle = getDirectionTo(getWayPoint()).angle();
        }
    }
}
