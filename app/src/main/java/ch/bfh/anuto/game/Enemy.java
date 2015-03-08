package ch.bfh.anuto.game;

import android.graphics.PointF;

import org.simpleframework.xml.Element;

public abstract class Enemy extends GameObject {
    /*
    ------ Members -----
     */

    @Element(name="path", required=false)
    protected Path mPath = null;

    protected int mNextWayPointIndex = 0;

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
}
