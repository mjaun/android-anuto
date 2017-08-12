package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

public class GameDescriptorRoot {

    @Element(name = "map")
    private String mMap;

    @Element(name = "wave")
    private int mWave;

    @Element(name = "lives")
    private int mLives;

    @Element(name = "credits")
    private int mCredits;

    @ElementList(inline = true, entry = "tower")
    private List<TowerDescriptor> mTowerDescriptors = new ArrayList<>();

    @ElementList(inline = true, entry = "enemy")
    private List<EnemyDescriptor> mEnemyDescriptors = new ArrayList<>();

}
