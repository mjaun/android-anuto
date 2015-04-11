package ch.bfh.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.objects.Plateau;
import ch.bfh.anuto.game.objects.Tower;
import ch.bfh.anuto.util.math.Vector2;

public class TowerDefenseView extends View implements GameEngine.Listener, View.OnDragListener, View.OnTouchListener {

    /*
    ------ Constants ------
     */

    private final static String TAG = TowerDefenseView.class.getName();

    /*
    ------ Members ------
     */

    private GameEngine mGame;

    /*
    ------ Constructors ------
     */

    public TowerDefenseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setOnDragListener(this);
        setOnTouchListener(this);
    }

    /*
    ------ Methods ------
    */

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

        if (mGame != null) {
            mGame.render(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Vector2 pos = mGame.getGameCoordinate(event.getX(), event.getY());

            Tower closest = (Tower)mGame.getGameObjects(Tower.TYPE_ID)
                    .min(GameObject.distanceTo(pos));

            if (closest != null && closest.getDistanceTo(pos) < 0.5f) {
                mGame.getManager().selectTower(closest);
            } else {
                mGame.getManager().selectTower(null);
            }
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
                    mGame.add(tower);
                    mGame.getManager().selectTower(tower);
                }
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                if (tower.getGame() != null) {
                    tower.remove();
                    mGame.getManager().selectTower(null);
                }
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                if (tower.getGame() != null) {
                    tower.setPosition(closestPlateau.getPosition());
                }
                break;

            case DragEvent.ACTION_DROP:
                if (tower.getGame() != null) {
                    tower.setPlateau(closestPlateau);
                    tower.buy();
                    mGame.getManager().selectTower(null);
                }
                break;
        }

        return true;
    }


    @Override
    public void onTick() {
        postInvalidate();
    }
}
