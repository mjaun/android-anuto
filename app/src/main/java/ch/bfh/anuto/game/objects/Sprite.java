package ch.bfh.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.util.math.Vector2;

public class Sprite extends DrawObject {

    /*
    ------ Static ------
     */

    private static HashMap<Integer, Sprite> sSpriteCache = new HashMap<>();

    public static Sprite fromResources(Resources res, int id) {
        return fromResources(res, id, 1);
    }

    public static Sprite fromResources(Resources res, int id, int count) {
        if (sSpriteCache.containsKey(id)) {
            return new Sprite(sSpriteCache.get(id));
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

        Sprite sprite = new Sprite(bmps);
        sSpriteCache.put(id, sprite);

        return fromResources(res, id, count);
    }

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onDraw(Sprite sprite, Canvas canvas);
    }

    /*
    ------ Animator Class ------
     */

    public static class Animator {
        private int mSeqIndex;
        private int[] mSequence;
        private TickTimer mTimer = new TickTimer();

        private long mLastTick;
        private GameEngine mGame;

        public Animator(GameEngine game) {
            mGame = game;
        }

        public void setSequence(int[] sequence) {
            mSequence = sequence;
            mSeqIndex = 0;
        }

        public void setSpeed(float speed) {
            mTimer.setFrequency(speed * mSequence.length);
        }

        private void tick() {
            if (mGame != null) {
                long tick = mGame.getTickCount();

                if (tick == mLastTick) {
                    return;
                }

                mLastTick = tick;
            }

            if (mTimer.tick()) {
                mSeqIndex++;

                if (mSeqIndex >= mSequence.length) {
                    mSeqIndex = 0;
                }
            }
        }

        private int getIndex() {
            return mSequence[mSeqIndex];
        }
    }

    /*
    ------ Members ------
     */

    private int mIndex;
    private int mLayer;

    private Animator mAnimator;
    private Listener mListener;

    private final List<Bitmap> mBitmaps;
    private final Matrix mMatrix = new Matrix();

    /*
    ------ Constructors ------
     */

    private Sprite(Bitmap... bitmaps) {
        mBitmaps = new ArrayList<Bitmap>(Arrays.asList(bitmaps));
        setMatrix(1f);
    }

    private Sprite(Sprite src) {
        mBitmaps = src.mBitmaps;
        mMatrix.set(src.mMatrix);
    }

    /*
    ------ Methods ------
     */

    @Override
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

    public int count() {
        return mBitmaps.size();
    }


    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix src) {
        mMatrix.set(src);
    }

    public void setMatrix(float length) {
        setMatrix(length, length, null);
    }

    public void setMatrix(Float width, Float height) {
        setMatrix(width, height, null);
    }

    public void setMatrix(Float width, Float height, Vector2 center) {
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


    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }


    public Animator getAnimator() {
        if (mAnimator == null) {
            mAnimator = new Animator(null);
        }

        return mAnimator;
    }

    public void setAnimator(Animator animator) {
        mAnimator = animator;
    }

    public int[] sequenceForward() {
        int ret[] = new int[count()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = i;
        }

        return ret;
    }

    public int[] sequenceForwardBackward() {
        int ret[] = new int[count() * 2 - 2];

        for (int i = 0; i < ret.length; i++) {
            if (i < count()) {
                ret[i] = i;
            } else {
                ret[i] = count() * 2 - 2 - i;
            }
        }

        return ret;
    }

    public void animate() {
        mAnimator.tick();
        mIndex = mAnimator.getIndex();
    }


    @Override
    public void onDraw(Canvas canvas) {
        mListener.onDraw(this, canvas);
        canvas.drawBitmap(mBitmaps.get(mIndex), mMatrix, null);
    }
}
