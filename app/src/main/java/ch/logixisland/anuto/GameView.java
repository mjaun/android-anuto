package ch.logixisland.anuto;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Plateau;
import ch.logixisland.anuto.game.objects.Tower;
import ch.logixisland.anuto.util.math.Vector2;

public class GameView extends View implements Runnable, View.OnDragListener, View.OnTouchListener {

    /*
    ------ Constants ------
     */

    private final static String TAG = GameView.class.getName();

    /*
    ------ Members ------
     */

    private GameEngine mGame;
    private GameManager mManager;

    /*
    ------ Constructors ------
     */

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setOnDragListener(this);
        setOnTouchListener(this);
    }

    /*
    ------ Methods ------
    */

    public void start() {
        mGame = GameEngine.getInstance();
        mManager = GameManager.getInstance();

        mGame.setScreenSize(getWidth(), getHeight());
        mGame.add(this);
    }

    public void stop() {
        mGame.remove(this);

        mGame = null;
        mManager = null;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mGame != null) {
            mGame.setScreenSize(w, h);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mGame != null) {
            mGame.draw(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGame == null) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && !mManager.isGameOver()) {
            Vector2 pos = mGame.screenToGame(new Vector2(event.getX(), event.getY()));

            Tower closest = (Tower)mGame.get(Tower.TYPE_ID)
                    .min(GameObject.distanceTo(pos));

            mManager.hideTowerInfo();
            if (closest != null && closest.getDistanceTo(pos) < 0.5f) {
                if (mManager.getSelectedTower() == closest) {
                    mManager.showTowerInfo(closest);
                } else {
                    mManager.setSelectedTower(closest);
                }
            } else {
                mManager.setSelectedTower(null);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (mGame == null) {
            return false;
        }

        Tower tower = (Tower)event.getLocalState();
        Vector2 pos = mGame.screenToGame(new Vector2(event.getX(), event.getY()));

        Plateau closestPlateau = mGame.get(Plateau.TYPE_ID)
                .cast(Plateau.class)
                .filter(Plateau.unoccupied())
                .min(GameObject.distanceTo(pos));

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                if (closestPlateau != null) {
                    mGame.add(tower);
                    mManager.setSelectedTower(tower);
                }
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                if (tower.isInGame()) {
                    tower.remove();
                    mManager.setSelectedTower(null);
                }
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                if (tower.isInGame()) {
                    tower.setPosition(closestPlateau.getPosition());
                }
                break;

            case DragEvent.ACTION_DROP:
                if (tower.isInGame()) {
                    tower.buy();
                    tower.setPlateau(closestPlateau);
                    tower.setEnabled(true);
                    mManager.setSelectedTower(null);
                }
                break;
        }

        return true;
    }

    @Override
    public void run() {
        postInvalidate();
    }
}
