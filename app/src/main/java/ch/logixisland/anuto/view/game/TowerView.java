package ch.logixisland.anuto.view.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import ch.logixisland.anuto.AnutoApplication;
import ch.logixisland.anuto.GameFactory;
import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.theme.Theme;
import ch.logixisland.anuto.entity.tower.Tower;

public class TowerView extends View {

    private final static float TEXT_SIZE = 20f;
    private final static float DRAW_SIZE = 1.3f;

    private Tower mPreviewTower;

    private int mTextColor;
    private int mTextColorDisabled;
    private final Paint mPaintText;
    private final Matrix mScreenMatrix;

    public TowerView(Context context, AttributeSet attrs) throws ClassNotFoundException {
        super(context, attrs);

        if (!isInEditMode()) {
            GameFactory factory = AnutoApplication.getInstance().getGameFactory();
            Theme theme = factory.getThemeManager().getTheme();
            mTextColor = theme.getColor(R.attr.textColor);
            mTextColorDisabled = theme.getColor(R.attr.textDisabledColor);
        }

        float density = context.getResources().getDisplayMetrics().density;
        mPaintText = new Paint();
        mPaintText.setColor(mTextColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(TEXT_SIZE * density);

        mScreenMatrix = new Matrix();
    }

    public void setEnabled(boolean enabled) {
        mPaintText.setColor(enabled ? mTextColor : mTextColorDisabled);
        postInvalidate();
    }

    public void setPreviewTower(Tower tower) {
        mPreviewTower = tower;
        postInvalidate();
    }

    public int getTowerValue() {
        if (mPreviewTower == null) {
            return 0;
        }

        return mPreviewTower.getValue();
    }

    public String getTowerName() {
        return mPreviewTower.getName();
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

}
