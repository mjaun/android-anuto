package ch.logixisland.anuto.engine.logic.map;

import org.simpleframework.xml.Root;

import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.util.math.Vector2;

@Root
public class MapPath {

    private final List<Vector2> mWayPoints;

    public MapPath(List<Vector2> wayPoints) {
        mWayPoints = wayPoints;
    }

    public List<Vector2> getWayPoints() {
        return Collections.unmodifiableList(mWayPoints);
    }

}
