package ch.bfh.anuto.game;

import android.graphics.PointF;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Complete;
import org.simpleframework.xml.core.Persist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {
    @Root(name="waypoint")
    private static class XmlWayPoint {
        public XmlWayPoint() {

        }

        public XmlWayPoint(PointF p) {
            x = p.x;
            y = p.y;
        }

        @Attribute
        public float x;
        @Attribute
        public float y;

        public PointF toPoint() {
            return new PointF(x, y);
        }
    }

    /*
    ------ Members ------
     */

    @ElementList(inline=true)
    private ArrayList<XmlWayPoint> mXmlWayPoints;

    private ArrayList<PointF> mWayPoints;

    /*
    ------ Constructors ------
     */

    public Path() {
        mWayPoints = new ArrayList<>();
    }

    public Path(PointF... wayPoints) {
        mWayPoints = new ArrayList<>(Arrays.asList(wayPoints));
    }

    /*
    ------ Public Methods ------
     */

    public List<PointF> getWayPoints() {
        return mWayPoints;
    }

    /*
    ------ XML Serialization ------
     */

    @Persist
    private void beforeXmlSerialize() {
        // not very beautiful, but we do this because serializing mWayPoints directly results
        // in an ugly XML which can't be changed because we do not own the PointF class

        mXmlWayPoints = new ArrayList<XmlWayPoint>();

        for (PointF p : mWayPoints) {
            mXmlWayPoints.add(new XmlWayPoint(p));
        }
    }

    @Complete
    private void afterXmlSerialize() {
        mXmlWayPoints = null;
    }

    @Commit
    private void afterXmlDeserialize() {
        for (XmlWayPoint p : mXmlWayPoints) {
            mWayPoints.add(p.toPoint());
        }

        mXmlWayPoints = null;
    }
}
