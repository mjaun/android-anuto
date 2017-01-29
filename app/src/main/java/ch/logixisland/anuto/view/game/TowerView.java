package ch.logixisland.anuto.view.game;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.business.control.TowerInserter;
import ch.logixisland.anuto.business.score.CreditsListener;
import ch.logixisland.anuto.business.score.ScoreBoard;
import ch.logixisland.anuto.engine.theme.ThemeManager;
import ch.logixisland.anuto.entity.tower.Tower;
import ch.logixisland.anuto.entity.tower.TowerFactory;

public class TowerView extends View implements View.OnTouchListener, View.OnDragListener {

    private final static float TEXT_SIZE = 20f;
    private final static float DRAW_SIZE = 1.3f;

    private final ThemeManager mThemeManager;
    private final TowerInserter mTowerInserter;
    private final ScoreBoard mScoreBoard;
    private final TowerFactory mTowerFactory;

    private Tower mPreviewTower;
    private int mTextColor;
    private int mTextColorDisabled;

    private final Paint mPaintText;
    private final Matrix mScreenMatrix;

    private CreditsListener mCreditsListener = new CreditsListener() {
        @Override
        public void creditsChanged(int credits) {
            if (mPreviewTower != null) {
                if (credits >= mPreviewTower.getValue()) {
                    mPaintText.setColor(mTextColor);
                } else {
                    mPaintText.setColor(mTextColorDisabled);
                }

                TowerView.this.postInvalidate();
            }
        }
    };

    public TowerView(Context context, AttributeSet attrs) throws ClassNotFoundException {
        super(context, attrs);

        if (!isInEditMode()) {
            GameFactory factory = AnutoApplication.getInstance().getGameFactory();
            mThemeManager = factory.getThemeManager();
            mScoreBoard = factory.getScoreBoard();
            mTowerInserter = factory.getTowerInserter();
            mTowerFactory = factory.getTowerFactory();

            mScoreBoard.addCreditsListener(mCreditsListener);
            mTextColor = mThemeManager.getColor(R.attr.textColor);
            mTextColorDisabled = mThemeManager.getColor(R.attr.textDisabledColor);
        } else {
            mThemeManager = null;
            mScoreBoard = null;
            mTowerInserter = null;
            mTowerFactory = null;
        }

        float density = context.getResources().getDisplayMetrics().density;
        mPaintText = new Paint();
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(TEXT_SIZE * density);

        mScreenMatrix = new Matrix();

        setOnTouchListener(this);
        setOnDragListener(this);
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
            mPreviewTower.preview(canvas);
            canvas.restore();

            canvas.drawText(Integer.toString(mPreviewTower.getValue()),
                    getWidth() / 2,
                    getHeight() / 2 - (mPaintText.ascent() + mPaintText.descent()) / 2,
                    mPaintText);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTowerInserter.insertTower(mPreviewTower.getName());

            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder() {
                @Override
                public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                }

                @Override
                public void onDrawShadow(Canvas canvas) {
                }
            };

            ClipData data = ClipData.newPlainText("", "");
            startDrag(data, shadowBuilder, this, 0);
        }

        return false;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
            if (event.getLocalState() == this) {
                return true;
            }
        }

        if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
            mTowerInserter.cancel();
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

}
