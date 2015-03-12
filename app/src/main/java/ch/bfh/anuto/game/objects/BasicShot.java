package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Projectile;
import ch.bfh.anuto.game.Tower;

public class BasicShot extends Projectile {
    protected final static float SPEED = 0.1f;

    public BasicShot(Tower owner, Enemy target) {
        super(owner, target);

        mPaint.setColor(Color.RED);
    }

    @Override
    public void tick() {
        if (getDistanceToTarget() < SPEED) {
            mGame.removeObject(this);
        }

        PointF dir = getDirectionToTarget();
        mPosition.offset(dir.x * SPEED, dir.y * SPEED);
    }

    @Override
    public void draw(Canvas canvas) {
        PointF pos = mGame.getPointOnScreen(mPosition);
        canvas.drawCircle(pos.x, pos.y, mGame.getTileSize() / 3, mPaint);
    }
}
