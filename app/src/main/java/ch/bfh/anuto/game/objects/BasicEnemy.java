package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Path;

public class BasicEnemy extends Enemy {
    private final static float SPEED = 0.05f;

    public BasicEnemy() {
        mPaint.setColor(Color.BLUE);
    }

    public BasicEnemy(PointF position, Path path) {
        this();

        setPosition(position);
        setPath(path);
    }

    @Override
    public void tick() {
        if (!hasWayPoint()) {
            return;
        }

        if (getDistanceToWayPoint() < SPEED) {
            setPosition(getWayPoint());
            nextWayPoint();
        }
        else {
            move(getDirectionToWayPoint(), SPEED);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawHealthBar(canvas);
        canvas.drawCircle(0f, 0f, 0.5f, mPaint);
    }
}
