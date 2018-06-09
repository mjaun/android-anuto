package ch.logixisland.anuto.data.map;

import android.content.res.Resources;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Root
public class GameMap {

    @Element(name = "id", required = false)
    private String mId;

    @Element(name = "width")
    private int mWidth;

    @Element(name = "height")
    private int mHeight;

    @ElementList(name = "plateaus", entry = "plateau")
    private List<PlateauInfo> mPlateaus = new ArrayList<>();

    @ElementList(name = "paths", entry = "path")
    private List<MapPath> mPaths = new ArrayList<>();

    public static GameMap fromXml(Serializer serializer, Resources resources, int resId, String mapId) throws Exception {
        InputStream stream = resources.openRawResource(resId);

        try {
            GameMap gameMap = serializer.read(GameMap.class, stream);
            gameMap.mId = mapId;
            return gameMap;
        } finally {
            stream.close();
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
