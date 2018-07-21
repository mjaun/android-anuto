package ch.logixisland.anuto.engine.render;

import android.graphics.Matrix;
import android.graphics.RectF;

import ch.logixisland.anuto.util.math.Vector2;

public class Viewport {

    private Matrix mScreenMatrix;
    private Matrix mScreenMatrixInverse;
    private float mGameWidth;
    private float mGameHeight;
    private float mScreenWidth;
    private float mScreenHeight;
    private RectF mScreenClipRect;

    public void setGameSize(int width, int height) {
        mGameWidth = width;
        mGameHeight = height;
        mScreenClipRect = new RectF(-0.5f, -0.5f, mGameWidth - 0.5f, mGameHeight - 0.5f);
        calcScreenMatrix();
    }

    public void setScreenSize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
        calcScreenMatrix();
    }

    public Matrix getScreenMatrix() {
        return mScreenMatrix;
    }

    public RectF getScreenClipRect() {
        return mScreenClipRect;
    }

    public Vector2 screenToGame(Vector2 pos) {
        float[] pts = {pos.x(), pos.y()};
        mScreenMatrixInverse.mapPoints(pts);
        return new Vector2(pts[0], pts[1]);
    }

    private void calcScreenMatrix() {
        mScreenMatrix = new Matrix();

        float tileSize = Math.min(mScreenWidth / mGameWidth, mScreenHeight / mGameHeight);
        mScreenMatrix.postTranslate(0.5f, 0.5f);
        mScreenMatrix.postScale(tileSize, tileSize);

        float paddingLeft = (mScreenWidth - (tileSize * mGameWidth)) / 2f;
        float paddingBottom = (mScreenHeight - (tileSize * mGameHeight));
        mScreenMatrix.postTranslate(paddingLeft, paddingBottom);

        mScreenMatrix.postScale(1f, -1f);
        mScreenMatrix.postTranslate(0, mScreenHeight);

        mScreenMatrixInverse = new Matrix();
        mScreenMatrix.invert(mScreenMatrixInverse);
    }

}
