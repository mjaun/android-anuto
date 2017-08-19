package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.ElementList;

import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.util.math.Vector2;

public class MineLayerDescriptor extends TowerDescriptor {

    @ElementList(name = "minePositions", entry = "position")
    List<Vector2> mMinePositions;

    public List<Vector2> getMinePositions() {
        return Collections.unmodifiableList(mMinePositions);
    }

    public void setMinePositions(List<Vector2> minePositions) {
        mMinePositions = minePositions;
    }
}
