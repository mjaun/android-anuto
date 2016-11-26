package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.ElementList;

import java.util.Iterator;
import java.util.List;

import ch.logixisland.anuto.util.math.vector.Vector2;

public class Path implements Iterable<Vector2> {

    /*
    ------ Fields ------
     */

    @ElementList(inline=true)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Vector2> wayPoints;

    /*
    ------ Methods ------
     */

    @Override
    public Iterator<Vector2> iterator() {
        return wayPoints.iterator();
    }

    public Vector2 get(int index) {
        return wayPoints.get(index);
    }

    public int size() {
        return wayPoints.size();
    }
}
