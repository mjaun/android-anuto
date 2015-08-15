package ch.bfh.anuto;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ch.bfh.anuto.game.GameManager;
import ch.bfh.anuto.game.objects.Tower;

public class TowerView extends View implements View.OnTouchListener {

    private final static float DRAW_SIZE = 1.3f;

    private Tower mTower;
    private GameManager mManager;

    private final Paint mPaintText;
    private final Matrix mScreenMatrix;

    @SuppressWarnings("unchecked")
    private final Class<? extends Tower> mTowerClass;

    public TowerView(Context context, AttributeSet attrs) throws ClassNotFoundException{
        super(context, attrs);

        if (!isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TowerView);
            mTowerClass = (Class<? extends Tower>)Class.forName(a.getString(R.styleable.TowerView_itemClass));
            a.recycle();

            float density = context.getResources().getDisplayMetrics().density;
            mPaintText = new Paint();
            mPaintText.setColor(Color.BLACK);
            mPaintText.setTextAlign(Paint.Align.CENTER);
            mPaintText.setTextSize(25f * density);

            mScreenMatrix = new Matrix();

            setOnTouchListener(this);

            newTower();

            mManager = GameManager.getInstance();
            // TODO: how to remove this thing?
            mManager.addListener(new GameManager.OnCreditsChangedListener() {
                @Override
                public void onCreditsChanged(int credits) {
                    if (credits >= mTower.getValue()) {
                        mPaintText.setColor(Color.BLACK);
                    } else {
                        mPaintText.setColor(Color.RED);
                    }

                    TowerView.this.invalidate();
                }
            });
        } else {
            mPaintText = null;
            mTowerClass = null;
            mScreenMatrix = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (!isInEditMode()) {
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInEditMode()) {
            Paint p = new Paint();
            p.setColor(Color.GRAY);

            canvas.save();
            canvas.concat(mScreenMatrix);
            mTower.drawPreview(canvas);
            canvas.restore();

            if (isEnabled()) {
                canvas.drawText(Integer.toString(mTower.getValue()),
                        getWidth() / 2,
                        getHeight() / 2 - (mPaintText.ascent() + mPaintText.descent()) / 2,
                        mPaintText);
            }
        } else {
            Paint p = new Paint();
            p.setColor(Color.GRAY);
            canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isEnabled() &&
                mManager.getCredits() >= mTower.getValue()) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder() {
                @Override
                public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                }

                @Override
                public void onDrawShadow(Canvas canvas) {
                }
            };

            ClipData data = ClipData.newPlainText("", "");
            this.startDrag(data, shadowBuilder, mTower, 0);
            newTower();

            return true;
        }

        return false;
    }


    private void newTower() {
        try {
            mTower = mTowerClass.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("Class " + mTowerClass.getName() + " has no default constructor!");
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate object!", e);
        }
    }
}
