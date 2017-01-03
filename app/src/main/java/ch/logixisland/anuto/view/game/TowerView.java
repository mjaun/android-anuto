package ch.logixisland.anuto.view.game;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.business.manager.GameManager;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.engine.render.theme.ThemeManager;
import ch.logixisland.anuto.entity.tower.TowerFactory;
import ch.logixisland.anuto.util.data.TowerConfig;

public class TowerView extends View implements View.OnTouchListener {

    private final static float TEXT_SIZE = 20f;
    private final static float DRAW_SIZE = 1.3f;

    private final ThemeManager mThemeManager;
    private final GameManager mGameManager;
    private final ScoreBoard mScoreBoard;
    private final TowerFactory mTowerFactory;

    private Tower mPreviewTower;

    private final Paint mPaintText;
    private final Matrix mScreenMatrix;

    private CreditsListener mCreditsListener = new CreditsListener() {
        @Override
        public void creditsChanged(int credits) {
            if (mPreviewTower != null) {
                if (credits >= mPreviewTower.getValue()) {
                    mPaintText.setColor(mThemeManager.getTheme().getTextColor());
                } else {
                    mPaintText.setColor(Color.RED);
                }

                TowerView.this.postInvalidate();
            }
        }
    };

    public TowerView(Context context, AttributeSet attrs) throws ClassNotFoundException{
        super(context, attrs);

        if (!isInEditMode()) {
            GameFactory factory = AnutoApplication.getInstance().getGameFactory();
            mThemeManager = factory.getThemeManager();
            mScoreBoard = factory.getScoreBoard();
            mGameManager = factory.getGameManager();
            mTowerFactory = factory.getTowerFactory();

            mScoreBoard.addCreditsListener(mCreditsListener);
        } else {
            mThemeManager = null;
            mScoreBoard = null;
            mGameManager = null;
            mTowerFactory = null;
        }

        float density = context.getResources().getDisplayMetrics().density;
        mPaintText = new Paint();
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(TEXT_SIZE * density);

        mScreenMatrix = new Matrix();

        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mScreenMatrix.reset();

        float tileSize = Math.min(w, h);
        mScreenMatrix.postTranslate(DRAW_SIZE / 2, DRAW_SIZE / 2);
        mScreenMatrix.postScale(tileSize / DRAW_SIZE, tileSize / DRAW_SIZE);

        float paddingLeft = (w - tileSize) / 2f;
        float paddingTop = (h - tileSize) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);

        mScreenMatrix.postScale(1f, -1f);
        mScreenMatrix.postTranslate(0, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPreviewTower != null) {
            canvas.save();
            canvas.concat(mScreenMatrix);
            canvas.translate(-mPreviewTower.getPosition().x, -mPreviewTower.getPosition().y);
            mPreviewTower.preview(canvas);
            canvas.restore();

            canvas.drawText(Integer.toString(mPreviewTower.getValue()),
                    getWidth() / 2,
                    getHeight() / 2 - (mPaintText.ascent() + mPaintText.descent()) / 2,
                    mPaintText);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mScoreBoard.getCredits() >= mPreviewTower.getValue() && !mGameManager.isGameOver()) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder() {
                    @Override
                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                    }

                    @Override
                    public void onDrawShadow(Canvas canvas) {
                    }
                };

                ClipData data = ClipData.newPlainText("", "");
                startDrag(data, shadowBuilder, newTower(), 0);
            }
        }

        return false;
    }

    public void setSlot(int slot) {
        mPreviewTower = mTowerFactory.createTower(slot);
        mCreditsListener.creditsChanged(mScoreBoard.getCredits());
        postInvalidate();
    }

    public void close() {
        mScoreBoard.removeCreditsListener(mCreditsListener);
    }

    private Tower newTower() {
        return mTowerFactory.createTower(mPreviewTower.getName());
    }
}
