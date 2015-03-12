package ch.bfh.anuto;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.InputStream;

import ch.bfh.anuto.game.Level;
import ch.bfh.anuto.game.objects.BasicTower;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameListener;


public class TowerDefenseView extends SurfaceView implements GameListener, SurfaceHolder.Callback {
    private final static String TAG = TowerDefenseView.class.getName();

    protected GameEngine mGame;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setFocusable(true);

        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            Level lvl = Level.deserialize(inStream);

            mGame = lvl.createGame(getHolder());
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");

        mGame.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface changed");

        mGame.setScreenBounds(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");

        try {
            mGame.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
