package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;

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
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.basic_tower);
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
    protected void onTargetLost() {
        nextTarget();
    }
}
