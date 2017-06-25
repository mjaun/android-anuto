package ch.logixisland.anuto.engine.render;

import android.graphics.Matrix;

import ch.logixisland.anuto.util.math.vector.Vector2;

public class Viewport {

    private Matrix mScreenMatrix = new Matrix();
    private Matrix mScreenMatrixInverse = new Matrix();
    private Vector2 mGameSize = new Vector2((float) 10, (float) 10);
    private Vector2 mScreenSize = new Vector2((float) 100, (float) 100);

    public void setGameSize(int width, int height) {
        mGameSize = new Vector2((float) width, (float) height);
        calcScreenMatrix();
    }

    public void setScreenSize(int width, int height) {
        mScreenSize = new Vector2((float) width, (float) height);
        calcScreenMatrix();
    }

    public Matrix getScreenMatrix() {
        return mScreenMatrix;
    }

    public Vector2 screenToGame(Vector2 pos) {
        float[] pts = {pos.x(), pos.y()};
        mScreenMatrixInverse.mapPoints(pts);
        return new Vector2(pts[0], pts[1]);
    }

    private void calcScreenMatrix() {
        mScreenMatrix.reset();

        float tileSize = Math.min(mScreenSize.x() / mGameSize.x(), mScreenSize.y() / mGameSize.y());
        mScreenMatrix.postTranslate(0.5f, 0.5f);
        mScreenMatrix.postScale(tileSize, tileSize);

        float paddingLeft = (mScreenSize.x() - (tileSize * mGameSize.x())) / 2f;
        float paddingTop = (mScreenSize.y() - (tileSize * mGameSize.y())) / 2f;
        mScreenMatrix.postTranslate(paddingLeft, paddingTop);

        mScreenMatrix.postScale(1f, -1f);
        mScreenMatrix.postTranslate(0, mScreenSize.y());

        mScreenMatrix.invert(mScreenMatrixInverse);
    }

}
