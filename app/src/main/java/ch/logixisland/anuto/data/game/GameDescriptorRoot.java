package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

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

    @ElementListUnion({
            @ElementList(name = "entity", entry = "enemy", type = EnemyDescriptor.class),
            @ElementList(name = "entity", entry = "tower", type = TowerDescriptor.class),
    })
    private List<EntityDescriptor> mEntityDescriptors = new ArrayList<>();

}
