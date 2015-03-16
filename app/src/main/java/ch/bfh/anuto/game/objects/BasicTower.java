package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.Tower;

public class BasicTower extends Tower {
    private final static int RELOAD_TIME = 20;
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
    public void tick() {
        super.tick();

        if (!hasTarget()) {
            nextTarget();
        }

        if (hasTarget()) {
            if (isReloaded()) {
                shoot(new BasicShot(this, mTarget));
            }

            mAngle = getAngleToTarget();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.rotate(mAngle);
        mSprite.draw(canvas);
    }

    @Override
    public void initResources(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_tower);
    }

    @Override
    protected void onTargetLost() {
        nextTarget();
    }
}
