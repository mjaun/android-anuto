package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.TargetedShot;
import ch.bfh.anuto.util.math.Vector2;

public class BasicShot extends TargetedShot {

    private final static float DAMAGE = 10f;
    private final static float MOVEMENT_SPEED = 3f;
    private final static float ROTATION_STEP = 360f / GameEngine.TARGET_FPS;

    private Sprite mSprite;
    private float mAngle = 0f;

    public BasicShot() {
        mSpeed = MOVEMENT_SPEED;
    }

    public BasicShot(Vector2 position, Enemy target) {
        this();

        setPosition(position);
        setTarget(target);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(this, res, R.drawable.basic_shot);
        mSprite.calcMatrix(0.33f);
        mGame.addDrawObject(mSprite, LAYER);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mSprite);
    }

    @Override
    public void onBeforeDraw(Sprite sprite, Canvas canvas) {
        super.onBeforeDraw(sprite, canvas);
        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        mDirection = getDirectionTo(mTarget);
        mAngle += ROTATION_STEP;

        super.tick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy)GameObject.closest(mGame.getGameObjects(Enemy.TYPE_ID), mPosition);

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
