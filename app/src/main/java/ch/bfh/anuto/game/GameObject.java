package ch.bfh.anuto.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class GameObject {
    protected Game mGame;
    protected PointF mPosition;

    protected Paint mPaint = new Paint();

    public GameObject(Game game, PointF position) {
        mGame = game;

        // TODO: should we make a copy here?
        mPosition = new PointF(position.x, position.y);
    }

    public PointF getPosition() {
        // TODO: should we make a copy here?
        return mPosition;
    }

    public abstract void tick();

    public abstract void draw(Canvas canvas);
}
