package ch.logixisland.anuto.engine.logic.map;

import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.util.container.KeyValueStore;

@Root
public class GameMap {

    private final String mId;
    private final int mWidth;
    private final int mHeight;
    private final List<PlateauInfo> mPlateaus = new ArrayList<>();
    private final List<MapPath> mPaths = new ArrayList<>();

    public GameMap(String id, KeyValueStore data) {
        mId = id;
        mWidth = data.getInt("width");
        mHeight = data.getInt("height");

        for (KeyValueStore plateauData : data.getStoreList("plateaus")) {
            mPlateaus.add(new PlateauInfo(plateauData));
        }

        for (KeyValueStore pathData : data.getStoreList("paths")) {
            mPaths.add(new MapPath(pathData.getVectorList("wayPoints")));
        }
    }

    public String getId() {
        return mId;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public Collection<PlateauInfo> getPlateaus() {
        return Collections.unmodifiableCollection(mPlateaus);
    }

    public List<MapPath> getPaths() {
        return Collections.unmodifiableList(mPaths);
    }
}
