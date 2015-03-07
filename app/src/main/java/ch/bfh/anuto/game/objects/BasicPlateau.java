package ch.bfh.anuto.game.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import ch.bfh.anuto.game.Game;
import ch.bfh.anuto.game.Plateau;

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
