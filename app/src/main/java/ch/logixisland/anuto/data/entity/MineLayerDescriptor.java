package ch.logixisland.anuto.data.entity;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ch.logixisland.anuto.util.math.Vector2;

public class MineLayerDescriptor extends TowerDescriptor {

    @ElementList(name = "minePositions", entry = "position", required = false)
    Collection<Vector2> mMinePositions = new ArrayList<>();

    public Collection<Vector2> getMinePositions() {
        return Collections.unmodifiableCollection(mMinePositions);
    }

    public void setMinePositions(Collection<Vector2> minePositions) {
        mMinePositions = minePositions;
    }
}
