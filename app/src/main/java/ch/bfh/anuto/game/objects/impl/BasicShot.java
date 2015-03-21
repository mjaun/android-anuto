package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.TargetedShot;

public class BasicShot extends TargetedShot {

    private final static int DAMAGE = 10;
    private final static float MOVEMENT_SPEED = 3f / GameEngine.TARGET_FPS;
    private final static float ROTATION_SPEED = 360f / GameEngine.TARGET_FPS;
    private final static float SPAWN_OFFSET = 0.9f;

    private Sprite mSprite;
    private float mAngle = 0f;

    public BasicShot() {
        mSpeed = MOVEMENT_SPEED;
    }

    public BasicShot(PointF position, Enemy target) {
        this();

        setPosition(position);
        setTarget(target);

        move(getDirectionTo(mTarget), SPAWN_OFFSET);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_shot);
        mSprite.getMatrix().postScale(0.33f, 0.33f);
    }

    @Override
    public void tick() {
        mDirection = getDirectionTo(mTarget);
        mAngle += ROTATION_SPEED;

        super.tick();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.rotate(mAngle);
        mSprite.draw(canvas);
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = getClosestEnemy();

        if (closest != null) {
            setTarget(closest);
        } else {
            remove();
        }
    }

    @Override
    protected void onTargetReached() {
        mTarget.damage(DAMAGE);
        remove();
    }
}
