package ch.logixisland.anuto.engine.render;

import android.graphics.Matrix;
import android.graphics.RectF;

import ch.logixisland.anuto.util.math.Vector2;

public class Viewport {

    private Matrix mScreenMatrix = new Matrix();
    private Matrix mScreenMatrixInverse = new Matrix();
    private float mGameWidth = 10;
    private float mGameHeight = 10;
    private float mScreenWidth = 100;
    private float mScreenHeight = 100;

    public void setGameSize(int width, int height) {
        mGameWidth = width;
        mGameHeight = height;
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
        return new RectF(-0.5f, -0.5f, mGameWidth - 0.5f, mGameHeight - 0.5f);
    }

    public Vector2 screenToGame(Vector2 pos) {
        float[] pts = {pos.x(), pos.y()};
        mScreenMatrixInverse.mapPoints(pts);
        return new Vector2(pts[0], pts[1]);
    }

    private void calcScreenMatrix() {
        mScreenMatrix.reset();

        float tileSize = Math.min(mScreenWidth / mGameWidth, mScreenHeight / mGameHeight);
        mScreenMatrix.postTranslate(0.5f, 0.5f);
        mScreenMatrix.postScale(tileSize, tileSize);

        float paddingLeft = (mScreenWidth - (tileSize * mGameWidth)) / 2f;
        float paddingTop = (mScreenHeight - (tileSize * mGameHeight)) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);

        mScreenMatrix.postScale(1f, -1f);
        mScreenMatrix.postTranslate(0, mScreenHeight);

        mScreenMatrix.invert(mScreenMatrixInverse);
    }

}
