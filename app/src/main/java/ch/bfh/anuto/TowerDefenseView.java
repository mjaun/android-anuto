package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

import java.io.InputStream;

import ch.bfh.anuto.game.data.Level;
import ch.bfh.anuto.game.objects.Tower;
import ch.bfh.anuto.game.objects.impl.BasicTower;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.objects.impl.LaserTower;
import ch.bfh.anuto.util.Vector2;


public class TowerDefenseView extends View implements GameEngine.Listener, View.OnDragListener {
    private final static String TAG = TowerDefenseView.class.getName();

    protected GameEngine mGame;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setOnDragListener(this);

        if (!isInEditMode()) {
            try {
                InputStream inStream = getResources().openRawResource(R.raw.level1);
                Level lvl = Level.deserialize(inStream);

                mGame = lvl.createGame(getResources());
                mGame.addListener(this);

                //lvl.startWave(mGame, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        if (!isInEditMode()) {
            mGame.setScreenSize(w, h);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInEditMode()) {
            mGame.render(canvas);
        }
    }

    @Override
    public void onRenderRequest() {
        postInvalidate();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Tower tower = (Tower)event.getLocalState();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                mGame.addGameObject(tower);
                tower.showRange();
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                mGame.removeGameObject(tower);
                break;

            case DragEvent.ACTION_DROP:
                tower.hideRange();
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                PointF pos = new PointF(event.getX(), event.getY());
                tower.setPosition(mGame.getGameCoordinate(pos).round());
                break;
        }

        return true;
    }
}
