package ch.logixisland.anuto.view.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.tower.TowerInserter;
import ch.logixisland.anuto.business.tower.TowerSelector;
import ch.logixisland.anuto.engine.render.Renderer;
import ch.logixisland.anuto.engine.render.Viewport;
import ch.logixisland.anuto.util.math.Vector2;

public class GameView extends View implements View.OnDragListener, View.OnTouchListener {

    private final Viewport mViewport;
    private final Renderer mRenderer;
    private final TowerSelector mTowerSelector;
    private final TowerInserter mTowerInserter;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            GameFactory factory = AnutoApplication.getInstance().getGameFactory();
            mViewport = factory.getViewport();
            mRenderer = factory.getRenderer();
            mTowerSelector = factory.getTowerSelector();
            mTowerInserter = factory.getTowerInserter();

            mRenderer.setView(this);
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

    public void close() {
        mRenderer.setView(null);
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
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));
            mTowerSelector.selectTowerAt(pos);
            return true;
        }

        return false;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
            if (event.getLocalState() instanceof TowerView) {
                if (event.getX() > 0 && event.getX() < getWidth() && event.getY() > 0 && event.getY() < getHeight()) {
                    Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));
                    mTowerInserter.setPosition(pos);
                }

                return true;
            }
        }

        if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
            Vector2 pos = mViewport.screenToGame(new Vector2(event.getX(), event.getY()));
            mTowerInserter.setPosition(pos);
        }

        if (event.getAction() == DragEvent.ACTION_DROP) {
            mTowerInserter.buyTower();
        }

        if (event.getAction() == DragEvent.ACTION_DRAG_EXITED || event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
            mTowerInserter.cancel();
        }

        return false;
    }

}
