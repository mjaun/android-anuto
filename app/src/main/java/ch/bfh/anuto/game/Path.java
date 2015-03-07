package ch.bfh.anuto.game;

import android.graphics.PointF;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {
    // TODO: needs to be optimized because serialization of PointF looks ugly
    @ElementList(inline=true)
    private ArrayList<PointF> mWayPoints;

    public Path() {
        mWayPoints = new ArrayList<>();
    }

    public Path(PointF... wayPoints) {
        mWayPoints = new ArrayList<>(Arrays.asList(wayPoints));
    }

    public List<PointF> getWayPoints() {
        return mWayPoints;
    }
}
