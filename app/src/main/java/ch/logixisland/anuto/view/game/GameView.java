package ch.logixisland.anuto.view.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.business.control.TowerInserter;
import ch.logixisland.anuto.business.control.TowerSelector;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GameView extends View implements View.OnDragListener, View.OnTouchListener {

    /*
    ------ Members ------
     */

    private final Viewport mViewport;
    private final Renderer mRenderer;
    private final TowerSelector mTowerSelector;
    private final TowerInserter mTowerInserter;

    /*
    ------ Constructors ------
     */

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            GameFactory factory = AnutoApplication.getInstance().getGameFactory();
            mViewport = factory.getViewport();
            mRenderer = factory.getRenderer();
            mTowerSelector = factory.getTowerSelector();
            mTowerInserter = factory.getTowerInserter();
        } else {
            mViewport = null;
            mRenderer = null;
            mTowerSelector = null;
            mTowerInserter = null;
        }

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

        if (!isInEditMode()) {
            mViewport.setScreenSize(w, h);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInEditMode()) {
            mRenderer.draw(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));
            mTowerSelector.selectTowerAt(pos);
            return true;
        }

        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Tower tower = (Tower)event.getLocalState();
        Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                mTowerInserter.insertTower(tower);
                mTowerInserter.setPosition(pos);
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                mTowerInserter.cancel();;
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                mTowerInserter.setPosition(pos);
                break;

            case DragEvent.ACTION_DROP:
                mTowerInserter.buyTower();
                break;
        }

        return true;
    }

}
