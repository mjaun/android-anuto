package ch.bfh.anuto;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ch.bfh.anuto.game.objects.Tower;

public class InventoryItemView extends View implements View.OnTouchListener {

    private boolean mRotate;
    private Drawable mDrawable;
    private Matrix mMatrix;
    private Paint mPaintText;
    private Class<? extends Tower> mItemClass;


    public InventoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.InventoryItemView);
        mRotate = a.getBoolean(R.styleable.InventoryItemView_rotate, false);
        mDrawable = a.getDrawable(R.styleable.InventoryItemView_itemDrawable);
        try {
            mItemClass = (Class<? extends Tower>)Class.forName(a.getString(R.styleable.InventoryItemView_itemClass));
        } catch (ClassNotFoundException e) {}
        a.recycle();

        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth() - 1, mDrawable.getIntrinsicHeight() - 1);

        mPaintText = new Paint();
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(70);

        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mMatrix = new Matrix();

        int dw, dh;
        if (!mRotate) {
            dw = mDrawable.getIntrinsicWidth();
            dh = mDrawable.getIntrinsicHeight();
        } else {
            dw = mDrawable.getIntrinsicHeight();
            dh = mDrawable.getIntrinsicWidth();

            mMatrix.postRotate(-90);
            mMatrix.postTranslate(0, dh);
        }

        float ratio = Math.min((float)w / dw, (float)h / dh);
        mMatrix.postScale(ratio, ratio);

        float paddingLeft = (w - dw * ratio) / 2f;
        float paddingTop = (h - dh * ratio) / 2f;

        mMatrix.postTranslate(paddingLeft, paddingTop);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.concat(mMatrix);
        mDrawable.draw(canvas);
        canvas.restore();

        canvas.drawText(Integer.toString(getItem().getValue()),
                getWidth() / 2,
                getHeight() / 2 - (mPaintText.ascent() + mPaintText.descent()) / 2,
                mPaintText);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isEnabled()) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            this.startDrag(data, shadowBuilder, getItem(), 0);

            return true;
        }

        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            mPaintText.setColor(Color.BLACK);
        } else {
            mPaintText.setColor(Color.RED);
        }
    }

    public Tower getItem() {
        Tower item;

        try {
            item = mItemClass.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("Class " + mItemClass.getName() + " has no default constructor!");
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate object!", e);
        }

        return item;
    }
}
