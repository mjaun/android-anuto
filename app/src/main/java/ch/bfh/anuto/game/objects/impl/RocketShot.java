package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.TargetedShot;
import ch.bfh.anuto.util.Vector2;

public class RocketShot extends TargetedShot {

    private final static int DAMAGE = 50;
    private final static float MOVEMENT_SPEED = 3f / GameEngine.TARGET_FPS;
    private final static float ROTATION_SPEED = 360f / GameEngine.TARGET_FPS;
    private final static float SPAWN_OFFSET = 0.9f;

    private Sprite mSprite;
    private float mAngle = 0f;

    public RocketShot() {
        mSpeed = MOVEMENT_SPEED;
    }

    public RocketShot(Vector2 position, Enemy target) {
        this();

        setPosition(position);
        setTarget(target);

        move(getDirectionTo(mTarget), SPAWN_OFFSET);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mSprite);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(this, res, R.drawable.rocket_shot, 4);
        mSprite.calcMatrix(1f);
        mGame.addDrawObject(mSprite, LAYER);
    }

    @Override
    public void tick() {
        mDirection = getDirectionTo(mTarget);
        mAngle += ROTATION_SPEED;

        super.tick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy) GameObject.closest(mGame.getGameObjects(Enemy.TYPE_ID), mPosition);

        if (closest == null) {
            mGame.removeGameObject(this);
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        mTarget.damage(DAMAGE);
        mGame.removeGameObject(this);
    }
}
