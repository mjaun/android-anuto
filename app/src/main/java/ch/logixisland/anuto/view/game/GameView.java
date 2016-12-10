package ch.logixisland.anuto.view.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.game.engine.GameEngine;
import ch.logixisland.anuto.game.GameFactory;
import ch.logixisland.anuto.game.business.GameManager;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.plateau.Plateau;
import ch.logixisland.anuto.game.entity.tower.Tower;
import ch.logixisland.anuto.game.render.Renderer;
import ch.logixisland.anuto.game.render.Viewport;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GameView extends View implements View.OnDragListener, View.OnTouchListener {

    /*
    ------ Members ------
     */

    private final Viewport mViewport;
    private final Renderer mRenderer;
    private final GameEngine mGameEngine;
    private final GameManager mGameManager;

    /*
    ------ Constructors ------
     */

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        GameFactory factory = AnutoApplication.getInstance().getGameFactory();
        mViewport = factory.getViewport();
        mRenderer = factory.getRenderer();
        mGameEngine = factory.getGameEngine();
        mGameManager = factory.getGameManager();

        setFocusable(true);
        setOnDragListener(this);
        setOnTouchListener(this);
    }

    /*
    ------ Methods ------
    */

    public void start() {
        mViewport.setScreenSize(getWidth(), getHeight());
        mRenderer.setView(this);
    }

    public void stop() {

    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewport.setScreenSize(w, h);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRenderer.draw(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !mGameManager.isGameOver()) {
            Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));

            Tower closest = (Tower) mGameEngine.get(Types.ENEMY)
                    .min(Entity.distanceTo(pos));

            mGameManager.hideTowerInfo();
            if (closest != null && closest.getDistanceTo(pos) < 0.5f) {
                if (mGameManager.getSelectedTower() == closest) {
                    mGameManager.showTowerInfo(closest);
                } else {
                    mGameManager.setSelectedTower(closest);
                }
            } else {
                mGameManager.setSelectedTower(null);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Tower tower = (Tower)event.getLocalState();
        Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));

        Plateau closestPlateau = mGameEngine.get(Types.ENEMY)
                .cast(Plateau.class)
                .filter(Plateau.unoccupied())
                .min(Entity.distanceTo(pos));

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                if (closestPlateau != null) {
                    mGameEngine.add(tower);
                    mGameManager.setSelectedTower(tower);
                }
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                tower.remove();
                mGameManager.setSelectedTower(null);
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                tower.setPosition(closestPlateau.getPosition());
                break;

            case DragEvent.ACTION_DROP:
                tower.buy();
                tower.setPlateau(closestPlateau);
                tower.setEnabled(true);
                mGameManager.setSelectedTower(null);
                break;
        }

        return true;
    }

}
