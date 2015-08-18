package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Iterator;

import ch.logixisland.anuto.util.math.Vector2;

public class Path implements Iterable<Vector2> {
    @ElementList(inline=true)
    private ArrayList<Vector2> mWayPoints;

    @Override
    public Iterator<Vector2> iterator() {
        return mWayPoints.iterator();
    }

    public Vector2 get(int index) {
        return mWayPoints.get(index);
    }

    public int count() {
        return mWayPoints.size();
    }
}
