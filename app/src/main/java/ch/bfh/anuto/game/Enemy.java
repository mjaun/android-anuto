package ch.bfh.anuto.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.simpleframework.xml.Element;

import java.util.ArrayList;

public abstract class Enemy extends GameObject {

    /*
    ------ Constants ------
     */

    private static final float HEALTHBAR_WIDTH = 1.0f;
    private static final float HEALTHBAR_HEIGHT = 0.1f;
    private static final float HEALTHBAR_OFFSET = 0.6f;

    /*
    ------ Members -----
     */

    @Element(name="path")
    protected Path mPath = null;
    protected int mNextWayPointIndex = 0;

    protected int mHealth = 100;
    protected int mHealthMax = 100;

    protected Paint mHealthBarBg;
    protected Paint mHealthBarFg;

    /*
    ------ Constructors ------
     */

    protected Enemy() {
        mHealthBarBg = new Paint();
        mHealthBarBg.setColor(Color.BLACK);
        mHealthBarFg = new Paint();
        mHealthBarFg.setColor(Color.GREEN);
    }

    /*
    ------ Public Methods ------
     */

    public PointF getWayPoint() {
        if (mPath == null || mPath.getWayPoints().size() <= mNextWayPointIndex)
            return null;

        return mPath.getWayPoints().get(mNextWayPointIndex);
    }

    public void nextWayPoint() {
        if (mPath.getWayPoints().size() > mNextWayPointIndex)
            mNextWayPointIndex++;
    }

    public float getDistanceToWayPoint() {
        PointF wp = getWayPoint();

        if (wp == null)
            return -1;

        return getDistanceTo(wp);
    }

    public PointF getDirectionToWayPoint() {
        PointF wp = getWayPoint();

        if (wp == null)
            return null;

        return getDirectionTo(wp);
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public void damage(int dmg) {
        mHealth -= dmg;

        if (mHealth <= 0) {
            mGame.removeObject(this);
        }
    }

    public void heal(int val) {
        mHealth += val;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(-HEALTHBAR_WIDTH/2f, -HEALTHBAR_OFFSET);

        canvas.drawRect(0, 0, HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT, mHealthBarBg);
        canvas.drawRect(0, 0, mHealth * HEALTHBAR_WIDTH / mHealthMax, HEALTHBAR_HEIGHT, mHealthBarFg);

        canvas.restore();
    }
}
