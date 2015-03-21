package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.AimingTower;

public class BasicTower extends AimingTower {
    private final static float RELOAD_TIME = 0.2f * GameEngine.TARGET_FPS;
    private final static float RANGE = 5f;

    private float mAngle;

    public BasicTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public BasicTower(PointF position) {
        this();
        setPosition(position);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_tower);
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.rotate(mAngle);
        mSprite.draw(canvas);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasTarget()) {
            if (isReloaded()) {
                shoot(new BasicShot(mPosition, mTarget));
            }

            mAngle = getAngleTo(mTarget);
        }
    }
}
