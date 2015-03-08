package ch.bfh.anuto;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.InputStream;

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

        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            Level lvl = Level.deserialize(inStream);

            mGame = lvl.createGame();
            mGame.addListener(this);

            mGame.addObject(new BasicTower(new PointF(5, 5)));

            lvl.startWave(mGame, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        mGame.calcScreenBounds(w, h);
    }

    public Game getGame() {
        return mGame;
    }
}
