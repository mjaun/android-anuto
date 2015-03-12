package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

import ch.bfh.anuto.game.Plateau;

public class BasicPlateau extends Plateau {
    RectF mRect;

    public BasicPlateau() {
        mPaint.setColor(Color.LTGRAY);
    }

    public BasicPlateau(PointF position) {
        this();
        setPosition(position);
    }

    @Override
    public void tick() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (mRect == null) {
            mRect = mGame.getBlockOnScreen(mPosition);
        }

        canvas.drawRect(mRect, mPaint);
    }
}
