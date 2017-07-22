package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Root
public class WaveDescriptor {

    @ElementList(name = "enemies", entry = "enemy")
    private List<EnemyDescriptor> mEnemies = new ArrayList<>();

    @Element(name = "waveReward", required = false)
    private int mWaveReward = 0;

    @Element(name = "extend", required = false)
    private int mExtend = 0;

    @Element(name = "maxExtend", required = false)
    private int mMaxExtend = 0;

    public List<EnemyDescriptor> getEnemies() {
        return Collections.unmodifiableList(mEnemies);
    }

    public int getExtend() {
        return mExtend;
    }

    public int getMaxExtend() {
        return mMaxExtend;
    }

    public int getWaveReward() {
        return mWaveReward;
    }

}
