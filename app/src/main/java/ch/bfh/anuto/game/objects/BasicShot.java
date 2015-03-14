package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Projectile;
import ch.bfh.anuto.game.Tower;

public class BasicShot extends Projectile {
    private final static float SPEED = 0.1f;
    private final static int DMG = 10;

    public BasicShot(Tower owner, Enemy target) {
        super(owner, target);

        mPaint.setColor(Color.RED);
    }

    @Override
    public void tick() {
        if (getDistanceToTarget() < SPEED) {
            mGame.removeObject(this);
            mTarget.damage(DMG);
        }

        PointF dir = getDirectionToTarget();
        mPosition.offset(dir.x * SPEED, dir.y * SPEED);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(0f, 0f, 0.2f, mPaint);
    }
}
