package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

public class LevelDescriptor {

    @Element(name = "width")
    private int mWidth;

    @Element(name = "height")
    private int mHeight;

    @ElementList(name = "plateaus", entry = "plateau")
    private List<PlateauDescriptor> mPlateaus = new ArrayList<>();

    @ElementList(name = "paths", entry = "path")
    private List<PathDescriptor> mPaths = new ArrayList<>();

    @ElementList(name = "waves", entry = "wave")
    private List<WaveDescriptor> mWaves = new ArrayList<>();

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public List<PlateauDescriptor> getPlateaus() {
        return mPlateaus;
    }

    public List<PathDescriptor> getPaths() {
        return mPaths;
    }

    public List<WaveDescriptor> getWaves() {
        return mWaves;
    }
}
