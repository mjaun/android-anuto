package ch.logixisland.anuto.engine.render.sprite;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Arrays;
import java.util.List;

import ch.logixisland.anuto.util.math.Vector2;

public class SpriteTemplate {

    private final List<Bitmap> mBitmaps;
    private final Matrix mMatrix = new Matrix();

    SpriteTemplate(Bitmap... bitmaps) {
        mBitmaps = Arrays.asList(bitmaps);
    }

    List<Bitmap> getBitmaps() {
        return mBitmaps;
    }

    int getBitmapCount() {
        return mBitmaps.size();
    }

    Matrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix src) {
        mMatrix.set(src);
    }

    public void setMatrix(Float width, Float height, Vector2 center, Float rotate) {
        float aspect = (float) mBitmaps.get(0).getWidth() / mBitmaps.get(0).getHeight();

        if (width == null && height == null) {
            height = 1f;
        }

        if (width == null) {
            width = height * aspect;
        }

        if (height == null) {
            height = width / aspect;
        }

        if (center == null) {
            center = new Vector2(width / 2, height / 2);
        }

        float scaleX = width / mBitmaps.get(0).getWidth();
        float scaleY = height / mBitmaps.get(0).getHeight();

        mMatrix.reset();

        mMatrix.postScale(1f, -1f);
        mMatrix.postTranslate(0f, mBitmaps.get(0).getHeight());

        mMatrix.postScale(scaleX, scaleY);
        mMatrix.postTranslate(-center.x(), -center.y());

        if (rotate != null) {
            mMatrix.postRotate(rotate);
        }
    }

}
