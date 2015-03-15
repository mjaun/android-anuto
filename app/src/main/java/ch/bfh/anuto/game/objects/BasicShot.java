package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.Shot;
import ch.bfh.anuto.game.Tower;

public class BasicShot extends Shot {
    private final static float SPEED = 0.1f;
    private final static int DMG = 10;

    public BasicShot(Tower owner, Enemy target) {
        super(owner, target);

        mPaint.setColor(Color.RED);
    }

    @Override
    public void tick() {
        try {
            if (getDistanceToTarget() < SPEED) {
                getTarget().damage(DMG);
                remove();
            } else {
                move(getDirectionToTarget(), SPEED);
            }
        } catch (NullPointerException e) {
            Log.d("test", "Error");
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(0f, 0f, 0.2f, mPaint);
    }

    @Override
    public void onTargetLost() {
        if (mOwner.hasTarget()) {
            setTarget(mOwner.getTarget());
        } else {
            remove();
        }
    }
}
