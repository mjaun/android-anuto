package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Plateau;

public class BasicPlateau extends Plateau {
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
        canvas.drawRect(mGame.getBlockOnScreen(mPosition), mPaint);
    }
}
