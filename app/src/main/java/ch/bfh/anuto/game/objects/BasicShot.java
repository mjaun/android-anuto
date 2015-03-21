package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Sprite;

public class BasicShot extends TargetedShot {
    private final static int DAMAGE = 10;
    private final static float SPAWN_OFFSET = 0.9f;
    private final static float MOVEMENT_SPEED = 3f / GameEngine.TARGET_FPS;
    private final static float ROTATION_SPEED = 360f / GameEngine.TARGET_FPS;

    private Sprite mSprite;
    private float mAngle = 0f;

    public BasicShot(Enemy target) {
        setTarget(target);
        move(getDirectionTo(mTarget.getPosition()), SPAWN_OFFSET);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_shot);
        mSprite.getMatrix().postScale(0.33f, 0.33f);
    }

    @Override
    public void tick() {
        if (getDistanceTo(mTarget.getPosition()) < MOVEMENT_SPEED) {
            ((Enemy)mTarget).damage(DAMAGE);
            remove();
        } else {
            move(getDirectionTo(mTarget.getPosition()), MOVEMENT_SPEED);
        }

        mAngle += ROTATION_SPEED;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.rotate(mAngle);
        mSprite.draw(canvas);
    }

    @Override
    public void onTargetLost() {
        for (GameObject obj : mGame.getObjects(Enemy.TYPEID)) {
            setTarget(obj);
            return;
        }

        remove();
    }
}
