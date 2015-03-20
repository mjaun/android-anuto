package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;

public class AreaTower extends Tower {

    private final static int RELOAD_TIME = 20;
    private final static float RANGE = 5f;

    private float mAngle;

    public AreaTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public AreaTower(PointF position) {
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
        mSprite.draw(canvas);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(res, R.drawable.area_tower);
    }

    @Override
    protected void onTargetLost() {
        nextTarget();
    }
}
