package ch.logixisland.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.util.math.Vector2;

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
            Sprite ret = new Sprite(sSpriteCache.get(id));

            if (ret.count() != count) {
                throw new IllegalArgumentException("This Sprite is already initialized with another count value!");
            }

            return ret;
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
        private int mPosition;
        private int[] mSequence;
        private long mLastTick = -1;
        private boolean mReset;

        private final TickTimer mTimer = new TickTimer();
        private final GameEngine mGame;

        public Animator() {
            mGame = GameEngine.getInstance();
        }

        public void setSequence(int[] sequence) {
            mSequence = sequence;
            reset();
        }

        public void setFrequency(float frequency) {
            mTimer.setFrequency(frequency * mSequence.length);
        }

        public void setInterval(float interval) {
            mTimer.setInterval(interval / mSequence.length);
        }

        public void reset() {
            mTimer.reset();
            mPosition = 0;
            mReset = true;
        }

        public int getIndex() {
            return mSequence[mPosition];
        }

        public int getPosition() {
            return mPosition;
        }

        public int count() {
            return mSequence.length;
        }

        public boolean tick() {
            long tick = mGame.getTickCount();
            if (tick == mLastTick) {
                return mReset;
            }
            mLastTick = tick;

            mReset = false;

            if (mTimer.tick()) {
                mPosition++;

                if (mPosition >= mSequence.length) {
                    mPosition = 0;
                    mReset = true;
                }
            }

            return mReset;
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
        mBitmaps = new ArrayList<>(Arrays.asList(bitmaps));
    }

    private Sprite(Sprite src) {
        mBitmaps = src.mBitmaps;
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

    public void setMatrix(Float width, Float height, Vector2 center, Float rotate) {
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

        if (rotate != null) {
            mMatrix.postRotate(rotate);
        }
    }


    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }


    public Animator getAnimator() {
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

    public int[] sequenceBackward() {
        int ret[] = new int[count()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = count() - 1 - i;
        }

        return ret;
    }

    public boolean animate() {
        boolean ret = mAnimator.tick();
        mIndex = mAnimator.getIndex();
        return ret;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        if (mListener != null) {
            mListener.onDraw(this, canvas);
        }

        canvas.drawBitmap(mBitmaps.get(mIndex), mMatrix, null);
        canvas.restore();
    }
}
