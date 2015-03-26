package ch.bfh.anuto.game.data;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.bfh.anuto.util.math.Vector2;

public class Path {

    /*
    ------ Members ------
     */

    @ElementList(inline=true)
    private ArrayList<Vector2> mWayPoints;

    /*
    ------ Constructors ------
     */

    public Path() {
        mWayPoints = new ArrayList<>();
    }

    public Path(Vector2... wayPoints) {
        mWayPoints = new ArrayList<>(Arrays.asList(wayPoints));
    }

    /*
    ------ Public Methods ------
     */

    public List<Vector2> getWayPoints() {
        return mWayPoints;
    }
}
