package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.ElementList;

import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.util.math.vector.Vector2;

public class PathDescriptor {

    @ElementList(inline=true)
    private List<Vector2> wayPoints;

    public List<Vector2> getWayPoints() {
        return Collections.unmodifiableList(wayPoints);
    }

}
