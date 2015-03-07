package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Tower;

public class BasicTower extends Tower {
    public final static int RELOAD_TIME = 20;

    public BasicTower(PointF position) {
        super(position);

        mPaint.setColor(Color.GREEN);
    }

    public int ticksUntilShot = RELOAD_TIME;

    @Override
    public void tick() {
        ticksUntilShot--;

        if (ticksUntilShot < 0) {
            ticksUntilShot = RELOAD_TIME;

            Enemy enemy = nextEnemy();
            if (enemy != null) {
                mGame.addObject(new BasicShot(this, enemy));
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(mGame.getBlockOnScreen(mPosition), mPaint);
    }
}
