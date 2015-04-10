package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import ch.bfh.anuto.game.data.Level;
import ch.bfh.anuto.game.objects.impl.BasicTower;
import ch.bfh.anuto.game.objects.impl.RocketTower;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.util.Vector2;


public class TowerDefenseView extends View implements GameEngine.Listener {
    private final static String TAG = TowerDefenseView.class.getName();

    protected GameEngine mGame;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //getHolder().addCallback(this);
        setFocusable(true);

        try {
            InputStream inStream = getResources().openRawResource(R.raw.level1);
            Level lvl = Level.deserialize(inStream);

            mGame = lvl.createGame(getResources());
            mGame.addListener(this);

            mGame.addGameObject(new BasicTower(new Vector2(5, 10)));
            mGame.addGameObject(new BasicTower(new Vector2(6, 10)));
            mGame.addGameObject(new BasicTower(new Vector2(5, 9)));
            mGame.addGameObject(new BasicTower(new Vector2(6, 9)));

            mGame.addGameObject(new RocketTower(new Vector2(5, 5)));
            mGame.addGameObject(new RocketTower(new Vector2(6, 5)));
            mGame.addGameObject(new RocketTower(new Vector2(7, 5)));
            mGame.addGameObject(new RocketTower(new Vector2(8, 5)));

            //mGame.addGameObject(new BasicTower(new Vector2(5, 4)));
            //mGame.addGameObject(new BasicTower(new Vector2(6, 4)));
            //mGame.addGameObject(new BasicTower(new Vector2(7, 4)));
            //mGame.addGameObject(new BasicTower(new Vector2(8, 4)));

            //mGame.addGameObject(new BasicTower(new Vector2(2, 6)));
            //mGame.addGameObject(new BasicTower(new Vector2(2, 5)));
            //mGame.addGameObject(new BasicTower(new Vector2(2, 4)));
            //mGame.addGameObject(new BasicTower(new Vector2(2, 3)));
            //mGame.addGameObject(new BasicTower(new Vector2(1, 6)));
            //mGame.addGameObject(new BasicTower(new Vector2(1, 5)));
            //mGame.addGameObject(new BasicTower(new Vector2(1, 4)));
            //mGame.addGameObject(new BasicTower(new Vector2(1, 3)));

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
