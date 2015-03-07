package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public class BasicPlateau extends Plateau {
    public BasicPlateau(Game game, PointF position) {
        super(game, position);

        mPaint.setColor(Color.LTGRAY);
    }

    @Override
    public void tick() {

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(mGame.getBlockOnScreen(mPosition), mPaint);
    }
}
