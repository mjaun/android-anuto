package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import ch.bfh.anuto.game.BasicEnemy;
import ch.bfh.anuto.game.BasicPlateau;
import ch.bfh.anuto.game.BasicTower;
import ch.bfh.anuto.game.Game;
import ch.bfh.anuto.game.GameListener;
import ch.bfh.anuto.game.Tower;


public class TowerDefenseView extends View implements GameListener {
    protected Game mGame;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int width = getResources().getInteger(R.integer.game_width);
        int height = getResources().getInteger(R.integer.game_height);

        mGame = new Game(width, height);
        mGame.addListener(this);

        mGame.addObject(new BasicPlateau(mGame, new PointF(1, 4)));
        mGame.addObject(new BasicPlateau(mGame, new PointF(1, 5)));
        mGame.addObject(new BasicPlateau(mGame, new PointF(1, 6)));
        mGame.addObject(new BasicTower(mGame, new PointF(1, 5)));
        mGame.addObject(new BasicEnemy(mGame, new PointF(5, 0)));
    }

    @Override
    public void onTickEvent(Game game) {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGame.draw(canvas);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        mGame.setScreenBounds(new Rect(0, 0, w, h));
    }

    public Game getGame() {
        return mGame;
    }
}
