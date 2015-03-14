package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Tower;

public class BasicTower extends Tower {
    private final static int RELOAD_TIME = 20;

    public BasicTower() {
        mPaint.setColor(Color.GREEN);
    }

    public BasicTower(PointF position) {
        this();
        setPosition(position);
    }

    public int ticksUntilShot = RELOAD_TIME;

    @Override
    public void tick() {
        if (ticksUntilShot <= 0) {
            Enemy enemy = nextEnemy();
            if (enemy != null) {
                mGame.addObject(new BasicShot(this, enemy));
                ticksUntilShot = RELOAD_TIME;
            }
        }
        else {
            ticksUntilShot--;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(TILE_RECT, mPaint);
    }
}
