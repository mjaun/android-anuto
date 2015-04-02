package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.objects.Plateau;
import ch.bfh.anuto.game.objects.Tower;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.util.iterator.StreamIterator;
import ch.bfh.anuto.util.math.Vector2;

public class TowerDefenseView extends View implements GameEngine.Listener, View.OnDragListener, View.OnTouchListener {
    private final static String TAG = TowerDefenseView.class.getName();

    private GameEngine mGame;
    private Tower mSelectedTower;

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            setFocusable(true);
            setOnDragListener(this);
            setOnTouchListener(this);
        }
    }

    public GameEngine getGame() {
        return mGame;
    }

    public void setGame(GameEngine game) {
        if (mGame != null) {
            mGame.removeListener(this);
        }

        mGame = game;

        if (mGame != null) {
            mGame.addListener(this);
        }
    }

    public void selectTower(float x, float y) {
        Vector2 pos = mGame.getGameCoordinate(x, y);

        Tower closest = (Tower)mGame.getGameObjects(Tower.TYPE_ID)
                .min(GameObject.distanceTo(pos));

        if (closest != null && closest.getDistanceTo(pos) < 0.5f) {
            selectTower(closest);
        } else {
            selectTower(null);
        }
    }

    public void selectTower(Tower tower) {
        if (mSelectedTower != null) {
            mSelectedTower.hideRange();
        }

        mSelectedTower = tower;

        if (mSelectedTower != null) {
            mSelectedTower.showRange();
        }
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

        if (!isInEditMode() && mGame != null) {
            mGame.render(canvas);
        }
    }

    @Override
    public void onRenderRequest() {
        postInvalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            selectTower(event.getX(), event.getY());
        }

        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Tower tower = (Tower)event.getLocalState();
        Vector2 pos = mGame.getGameCoordinate(event.getX(), event.getY());

        Plateau closestPlateau = mGame.getGameObjects(Plateau.TYPE_ID)
                .cast(Plateau.class)
                .filter(Plateau.unoccupied())
                .min(GameObject.distanceTo(pos));

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                if (closestPlateau != null) {
                    mGame.addGameObject(tower);
                    selectTower(tower);
                }
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                if (tower.getGame() != null) {
                    selectTower(null);
                    mGame.removeGameObject(tower);
                }
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                if (tower.getGame() != null) {
                    tower.setPosition(closestPlateau.getPosition());
                }
                break;

            case DragEvent.ACTION_DROP:
                if (tower.getGame() != null) {
                    tower.setPosition(closestPlateau);

                    tower.setEnabled(true);
                    selectTower(null);
                }
                break;
        }

        return true;
    }
}
