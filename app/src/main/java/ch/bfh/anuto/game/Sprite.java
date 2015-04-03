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

import ch.bfh.anuto.util.math.Vector2;

public class Sprite extends DrawObject {

    /*
    ------ Static ------
     */

    private static HashMap<Integer, Sprite> spriteCache = new HashMap<>();

    public static Sprite fromResources(GameObject owner, int id) {
        return fromResources(owner, id, 1);
    }

    public static Sprite fromResources(GameObject owner, int id, int count) {
        if (spriteCache.containsKey(id)) {
            Sprite ret = new Sprite(spriteCache.get(id));
            ret.mOwner = owner;
            return ret;
        }

        Resources res = owner.getGame().getResources();
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

        Sprite sprite = new Sprite(bmps);
        spriteCache.put(id, sprite);

        return fromResources(owner, id, count);
    }

    /*
    ------ Members ------
     */

    private int mIndex = 0;
    private int mLayer = 0;
    private boolean mCycleBackwards = false;

    private GameObject mOwner;

    private final List<Bitmap> mBitmaps;
    private final Matrix mMatrix = new Matrix();

    /*
    ------ Constructors ------
     */

    private Sprite(Bitmap... bitmaps) {
        mBitmaps = new ArrayList<Bitmap>(Arrays.asList(bitmaps));
        calcMatrix();
    }

    private Sprite(Sprite src) {
        mBitmaps = src.mBitmaps;
        mMatrix.set(src.mMatrix);
    }

    /*
    ------ Methods ------
     */

    public GameObject getOwner() {
        return mOwner;
    }


    public int getLayer() {
        return mLayer;
    }

    public void setLayer(int layer) {
        mLayer = layer;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }


    public void calcMatrix() {
        calcMatrix(1f, 1f, null);
    }

    public void calcMatrix(float length) {
        calcMatrix(length, length, null);
    }

    public void calcMatrix(Float width, Float height) {
        calcMatrix(width, height, null);
    }

    public void calcMatrix(Float width, Float height, Vector2 center) {
        float aspect = (float)mBitmaps.get(0).getWidth() / mBitmaps.get(0).getHeight();

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
        mMatrix.postTranslate(-center.x, -center.y);
    }

    public Matrix getMatrix() {
        return mMatrix;
    }


    public int getCount() {
        return mBitmaps.size();
    }

    public void cycle() {
        mIndex++;

        if (mIndex >= mBitmaps.size()) {
            mIndex = 0;
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


    @Override
    public void draw(Canvas canvas) {
        if (mOwner != null) {
            mOwner.onDraw(this, canvas);
        }

        canvas.drawBitmap(mBitmaps.get(mIndex), mMatrix, null);
    }
}
