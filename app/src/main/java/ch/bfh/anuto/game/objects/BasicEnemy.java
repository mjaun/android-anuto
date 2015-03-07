package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import ch.bfh.anuto.game.Enemy;
import ch.bfh.anuto.game.Game;

public class BasicEnemy extends Enemy {
    protected final static float SPEED = 0.05f;

    public BasicEnemy(Game game, PointF position) {
        super(game, position);

        mPaint.setColor(Color.BLUE);
    }

    @Override
    public void tick() {
        mPosition.y += SPEED;

        if (!mGame.isPointInBounds(mPosition)) {
            mPosition.y = 0f;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        PointF p = mGame.getPointOnScreen(mPosition);
        canvas.drawCircle(p.x, p.y, mGame.getBlockLength() / 2, mPaint);
    }
}
