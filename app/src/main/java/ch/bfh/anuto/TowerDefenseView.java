package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import ch.bfh.anuto.game.Level;
import ch.bfh.anuto.game.Path;
import ch.bfh.anuto.game.Wave;
import ch.bfh.anuto.game.objects.BasicEnemy;
import ch.bfh.anuto.game.objects.BasicPlateau;
import ch.bfh.anuto.game.objects.BasicTower;
import ch.bfh.anuto.game.Game;
import ch.bfh.anuto.game.GameListener;


public class TowerDefenseView extends View implements GameListener {
    protected Game mGame;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int width = getResources().getInteger(R.integer.game_width);
        int height = getResources().getInteger(R.integer.game_height);

        Level test = new Level();
        test.getPlateaus().add(new BasicPlateau(new PointF(1, 4)));
        test.getPlateaus().add(new BasicPlateau(new PointF(1, 5)));
        test.getPlateaus().add(new BasicPlateau(new PointF(1, 6)));
        Path p = new Path(new PointF(1, 2), new PointF(3, 4));
        test.getPaths().add(p);
        test.getWaves().add(new Wave(new BasicEnemy(new PointF(2, 4), p)));
        test.serialize();

        mGame = new Game(width, height);
        mGame.addListener(this);

        mGame.addObject(new BasicPlateau(new PointF(1, 4)));
        mGame.addObject(new BasicPlateau(new PointF(1, 5)));
        mGame.addObject(new BasicPlateau(new PointF(1, 6)));
        mGame.addObject(new BasicTower(new PointF(1, 5)));
        mGame.addObject(new BasicEnemy(new PointF(5, 0), null));
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
