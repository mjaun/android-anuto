package ch.logixisland.anuto.engine.render.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import ch.logixisland.anuto.engine.render.Drawable;

public abstract class SpriteInstance implements Drawable {

    private final int mLayer;
    private final SpriteTemplate mTemplate;

    private Paint mPaint;
    private SpriteTransformation mListener;

    SpriteInstance(int layer, SpriteTemplate template) {
        mLayer = layer;
        mTemplate = template;
    }

    SpriteTemplate getTemplate() {
        return mTemplate;
    }

    abstract int getIndex();

    public void setListener(SpriteTransformation listener) {
        mListener = listener;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    @Override
    public int getLayer() {
        return mLayer;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        if (mListener != null) {
            mListener.draw(this, canvas);
        }

        Bitmap bitmap = mTemplate.getBitmaps().get(getIndex());
        Matrix matrix = mTemplate.getMatrix();
        canvas.drawBitmap(bitmap, matrix, mPaint);
        canvas.restore();
    }

}
