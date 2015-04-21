package ch.bfh.anuto.game.data;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.anuto.util.math.Vector2;

public class Path {
    @ElementList(inline=true)
    private ArrayList<Vector2> mWayPoints;

    public List<Vector2> getWayPoints() {
        return mWayPoints;
    }
}
