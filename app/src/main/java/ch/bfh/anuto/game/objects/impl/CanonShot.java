package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.game.objects.TargetedShot;
import ch.bfh.anuto.util.math.Vector2;

public class CanonShot extends TargetedShot {

    private final static float DAMAGE = 60f;
    private final static float MOVEMENT_SPEED = 4.0f;
    private final static float ROTATION_STEP = 360f / GameEngine.TARGET_FPS;

    private Sprite mSprite;
    private float mAngle = 0f;

    public CanonShot() {
        mSpeed = MOVEMENT_SPEED;
    }

    public CanonShot(Vector2 position, Enemy target) {
        this();

        setPosition(position);
        setTarget(target);
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.canon_shot, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom().nextInt(4));
        mSprite.setMatrix(0.33f);
        mSprite.setLayer(Layers.SHOT);
        mGame.add(mSprite);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void onTick() {
        mDirection = getDirectionTo(mTarget);
        mAngle += ROTATION_STEP;

        super.onTick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy)mGame.getGameObjects(Enemy.TYPE_ID)
                .min(GameObject.distanceTo(mPosition));

        if (closest == null) {
            this.remove();
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        mTarget.damage(DAMAGE);
        this.remove();
    }
}
