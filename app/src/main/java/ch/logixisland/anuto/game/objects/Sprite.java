package ch.logixisland.anuto.game.objects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.util.math.Vector2;

public class Sprite {

    /*
    ------ Static ------
     */

    private static HashMap<Integer, Sprite> sSpriteCache = new HashMap<>();

    public static Sprite fromResources(int id, int count) {
        if (sSpriteCache.containsKey(id)) {
            return new Sprite(sSpriteCache.get(id));
        } else {

            Resources res = GameEngine.getInstance().getResources();
            Bitmap[] bmps;

            if (count > 1) {
                Bitmap sheet = BitmapFactory.decodeResource(res, id);
                bmps = new Bitmap[count];
                int spriteWidth = sheet.getWidth() / count;
                int spriteHeight = sheet.getHeight();

                for (int i = 0; i < count; i++) {
                    bmps[i] = Bitmap.createBitmap(sheet, spriteWidth * i, 0, spriteWidth, spriteHeight);
                }
            } else {
                bmps = new Bitmap[1];
                bmps[0] = BitmapFactory.decodeResource(res, id);
            }

            sSpriteCache.put(id, new Sprite(bmps));
            return fromResources(id, count);
        }
    }

    /*
    ------ Listener Interface ------
     */

    public interface Listener {
        void onDraw(DrawObject sprite, Canvas canvas);
    }

    /*
    ------ Instance Classes ------
     */

    public abstract class Instance extends DrawObject {
        private final int mLayer;

        private Paint mPaint;
        private Listener mListener;

        private Instance(int layer) {
            mLayer = layer;
        }

        public Paint getPaint() {
            return mPaint;
        }

        public void setPaint(Paint paint) {
            mPaint = paint;
        }

        public Listener getListener() {
            return mListener;
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        public Instance copycat() {
            return new Instance(mLayer) {
                @Override
                public int getIndex() {
                    return Instance.this.getIndex();
                }
            };
        }

        @Override
        public int getLayer() {
            return mLayer;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();

            if (mListener != null) {
                mListener.onDraw(this, canvas);
            }

            canvas.drawBitmap(mBitmaps.get(getIndex()), mMatrix, mPaint);
            canvas.restore();
        }

        public abstract int getIndex();
    }

    public class FixedInstance extends Instance {

        private int mIndex;

        private FixedInstance(int layer) {
            super(layer);
        }

        @Override
        public int getIndex() {
            return mIndex;
        }

        public void setIndex(int index) {
            mIndex = index;
        }
    }

    public class AnimatedInstance extends FixedInstance {

        private int mPosition;
        private int[] mSequence;

        private final TickTimer mTimer = new TickTimer();


        private AnimatedInstance(int layer) {
            super(layer);
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
        }

        public int getIndex() {
            return mSequence[mPosition];
        }

        public int getSequencePosition() {
            return mPosition;
        }

        public int getSequenceLength() {
            return mSequence.length;
        }

        public boolean tick() {
            boolean ret = false;

            if (mTimer.tick()) {
                if (mPosition >= mSequence.length - 1) {
                    mPosition = 0;
                    ret = true;
                } else {
                    mPosition++;
                }
            }

            return ret;
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
    }

    /*
    ------ Members ------
     */

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


    public int count() {
        return mBitmaps.size();
    }


    public FixedInstance yieldStatic(int layer) {
        return new FixedInstance(layer);
    }

    public AnimatedInstance yieldAnimated(int layer) {
        return new AnimatedInstance(layer);
    }
}
