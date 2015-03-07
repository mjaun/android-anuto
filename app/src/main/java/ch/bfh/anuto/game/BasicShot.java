package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class BasicShot extends Projectile {
    protected final static float SPEED = 0.1f;

    public BasicShot(Game game, Tower owner, Enemy target) {
        super(game, owner, target);

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
        canvas.drawCircle(pos.x, pos.y, mGame.getBlockLength() / 3, mPaint);
    }
}
