package ch.logixisland.anuto;

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

import ch.logixisland.anuto.game.GameManager;
import ch.logixisland.anuto.game.objects.Tower;

public class TowerView extends View implements View.OnTouchListener {

    private final static float TEXT_SIZE = 20f;

    private Tower mTower;
    private Class<? extends Tower> mTowerClass;
    private GameManager mManager;

    private float mDrawSize = 1f;

    private final Paint mPaintText;
    private final Matrix mScreenMatrix;

    private GameManager.Listener mCreditsListener = new GameManager.OnCreditsChangedListener() {
        @Override
        public void onCreditsChanged(int credits) {
            if (mTower != null) {
                if (credits >= mTower.getValue()) {
                    mPaintText.setColor(Color.BLACK);
                } else {
                    mPaintText.setColor(Color.RED);
                }

                TowerView.this.postInvalidate();
            }
        }
    };

    public TowerView(Context context, AttributeSet attrs) throws ClassNotFoundException{
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TowerView);
        mDrawSize = a.getFloat(R.styleable.TowerView_drawSize, mDrawSize);
        a.recycle();

        float density = context.getResources().getDisplayMetrics().density;
        mPaintText = new Paint();
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(TEXT_SIZE * density);

        mScreenMatrix = new Matrix();

        if (!isInEditMode()) {
            mManager = GameManager.getInstance();
            mManager.addListener(mCreditsListener);
        }

        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mScreenMatrix.reset();

        float tileSize = Math.min(w, h);
        mScreenMatrix.postTranslate(mDrawSize / 2, mDrawSize / 2);
        mScreenMatrix.postScale(tileSize / mDrawSize, tileSize / mDrawSize);

        float paddingLeft = (w - tileSize) / 2f;
        float paddingTop = (h - tileSize) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);

        mScreenMatrix.postScale(1f, -1f);
        mScreenMatrix.postTranslate(0, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            canvas.drawColor(Color.GRAY);
        }

        if (mTower != null) {
            canvas.save();
            canvas.concat(mScreenMatrix);
            canvas.translate(-mTower.getPosition().x, -mTower.getPosition().y);
            mTower.preview(canvas);
            canvas.restore();

            if (isEnabled()) {
                canvas.drawText(Integer.toString(mTower.getValue()),
                        getWidth() / 2,
                        getHeight() / 2 - (mPaintText.ascent() + mPaintText.descent()) / 2,
                        mPaintText);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isEnabled() && mTowerClass != null && mManager.getCredits() >= mTower.getValue() &&
                    !mManager.isGameOver()) {
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


    public Class<? extends Tower> getTowerClass() {
        return mTowerClass;
    }

    public void setTowerClass(Class<? extends Tower> clazz) {
        mTowerClass = clazz;

        if (mTowerClass != null) {
            setTower(newTower());
        } else {
            setTower(null);
        }
    }

    public void setTowerClass(String className) throws ClassNotFoundException {
        mTowerClass = (Class<? extends Tower>) Class.forName(className);
        newTower();
    }

    public void setTower(Tower tower) {
        mTower = tower;
        this.postInvalidate();
    }


    public void close() {
        mManager.removeListener(mCreditsListener);
    }


    private Tower newTower() {
        try {
            return mTowerClass.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("Class " + mTowerClass.getName() + " has no default constructor!");
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate object!", e);
        }
    }
}
