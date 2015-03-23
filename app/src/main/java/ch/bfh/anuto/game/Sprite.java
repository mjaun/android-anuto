package ch.bfh.anuto.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.bfh.anuto.util.RemovedMark;

public class Sprite implements RemovedMark {

    /*
    ------ Static ------
     */

    private static HashMap<Integer, Sprite> spriteCache = new HashMap<>();

    public static Sprite fromResources(Resources res, int id) {
        return fromResources(res, id, 1);
    }

    public static Sprite fromResources(Resources res, int id, int count) {
        if (spriteCache.containsKey(id)) {
            return new Sprite(spriteCache.get(id));
        }

        Bitmap[] bmps;

        if (count > 1) {
            Bitmap sheet = BitmapFactory.decodeResource(res, id);
            bmps = new Bitmap[count];
            int spriteWidth = sheet.getWidth() / count;
            int spriteHeight = sheet.getHeight();

            for (int i = 0; i < count; i++) {
                bmps[i] = Bitmap.createBitmap(sheet, spriteWidth * i, 0, spriteWidth, spriteHeight);
            }
        }
        else {
            bmps = new Bitmap[1];
            bmps[0] = BitmapFactory.decodeResource(res, id);
        }

        float scale = 1f / bmps[0].getWidth();
        float height = bmps[0].getHeight() * scale;

        Matrix m = new Matrix();
        m.postScale(scale, scale);
        m.postTranslate(-0.5f, 0.5f - height);

        Sprite sprite = new Sprite(id, m, bmps);
        spriteCache.put(id, sprite);
        return new Sprite(sprite);
    }

    /*
    ------ Members ------
     */

    private final int mResourceId;
    private final Matrix mMatrix;
    private final List<Bitmap> mBitmaps;
    private int mLayer = 0;
    private int mIndex = 0;
    private float mCycleCounter = 0;
    private boolean mCycleBackwards = false;
    private boolean mRemovedMark = false;

    /*
    ------ Constructors ------
     */

    private Sprite(int resId, Matrix matrix, Bitmap... bitmaps) {
        mResourceId = resId;
        mMatrix = matrix;
        mBitmaps = new ArrayList<Bitmap>(Arrays.asList(bitmaps));
    }

    private Sprite(Sprite src) {
        mResourceId = src.mResourceId;
        mMatrix = new Matrix(src.mMatrix);
        mBitmaps = src.mBitmaps;
    }

    /*
    ------ Methods ------
     */

    public void select(int index) {
        mIndex = index;
    }

    public void cycle() {
        mIndex++;

        if (mIndex >= mBitmaps.size()) {
            mIndex = 0;
        }
    }

    public void cycle(float speed) {
        mCycleCounter += speed * mBitmaps.size();

        if (mCycleCounter > 1f) {
            cycle();
            mCycleCounter = 0;
        }
    }

    public void cycle2() {
        if (mCycleBackwards) {
            mIndex--;

            if (mIndex < 0) {
                mIndex = 1;
                mCycleBackwards = false;
            }
        } else {
            mIndex++;

            if (mIndex >= mBitmaps.size()) {
                mIndex = mBitmaps.size() - 2;
                mCycleBackwards = true;
            }
        }
    }

    public void cycle2(float speed) {
        mCycleCounter += speed * (mBitmaps.size() * 2 - 2);

        if (mCycleCounter > 1f) {
            cycle2();
            mCycleCounter = 0;
        }
    }


    public int getLayer() {
        return mLayer;
    }

    public void setLayer(int layer) {
        mLayer = layer;
    }


    public Matrix getMatrix() {
        return mMatrix;
    }

    public List<Bitmap> getBitmaps() {
        return mBitmaps;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmaps.get(mIndex), mMatrix, null);
    }


    @Override
    public void resetRemovedMark() {
        mRemovedMark = false;
    }

    @Override
    public void markAsRemoved() {
        mRemovedMark = true;
    }

    @Override
    public boolean hasRemovedMark() {
        return mRemovedMark;
    }
}
