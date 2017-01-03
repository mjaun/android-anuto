package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.tower.Tower;

public class LevelDescriptor {

    @Element(name="width")
    private int mWidth;

    @Element(name="height")
    private int mHeight;

    @ElementList(name="plateaus", entry="plateau")
    private List<PlateauDescriptor> mPlateaus = new ArrayList<>();

    @ElementList(name="paths", entry="path")
    private List<PathDescriptor> mPaths = new ArrayList<>();

    @ElementList(name="waves", entry="wave")
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
