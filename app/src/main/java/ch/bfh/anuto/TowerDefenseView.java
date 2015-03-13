package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.InputStream;

import ch.bfh.anuto.game.Level;
import ch.bfh.anuto.game.objects.BasicTower;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameListener;


public class TowerDefenseView extends View implements GameListener {
    private final static String TAG = TowerDefenseView.class.getName();

    protected GameEngine mGame;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //getHolder().addCallback(this);
        setFocusable(true);

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

    public GameEngine getGame() {
        return mGame;
    }

    public void start() {
        mGame.start();
    }

    public void stop() {
        mGame.stop();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGame.setScreenSize(w, h);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGame.render(canvas);
    }

    @Override
    public void onRenderRequest() {
        postInvalidate();
    }
}
