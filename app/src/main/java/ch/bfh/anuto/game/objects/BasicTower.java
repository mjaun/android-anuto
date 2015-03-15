package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Tower;

public class BasicTower extends Tower {
    private final static int RELOAD_TIME = 20;
    private final static float RANGE = 5f;

    public BasicTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;

        mPaint.setColor(Color.GREEN);
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

        if (hasTarget() && isReloaded()) {
            shoot(new BasicShot(this, getTarget()));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(TILE_RECT, mPaint);
    }

    @Override
    protected void onTargetLost() {
        nextTarget();
    }
}
