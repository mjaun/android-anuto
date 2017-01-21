package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.util.math.vector.Vector2;

public class PathDescriptor {

    @ElementList(entry = "wayPoint", inline = true)
    private List<Vector2> wayPoints = new ArrayList<>();

    public List<Vector2> getWayPoints() {
        return Collections.unmodifiableList(wayPoints);
    }

}
