package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnemyConfig {

    @Element(name = "name")
    private String mName;

    @Element(name = "health")
    private float mHealth;

    @Element(name = "speed")
    private float mSpeed;

    @Element(name = "reward")
    private int mReward;

    @ElementList(entry = "weakAgainst", inline = true, required = false)
    private List<WeaponType> mWeakAgainst = new ArrayList<>();

    @ElementList(entry = "strongAgainst", inline = true, required = false)
    private List<WeaponType> mStrongAgainst = new ArrayList<>();

    @ElementMap(entry = "property", key = "name", inline = true, required = false, attribute = true)
    private Map<String, Float> mProperties = new HashMap<>();

    public String getName() {
        return mName;
    }

    public float getHealth() {
        return mHealth;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public int getReward() {
        return mReward;
    }

    public Collection<WeaponType> getWeakAgainst() {
        return mWeakAgainst;
    }

    public Collection<WeaponType> getStrongAgainst() {
        return mStrongAgainst;
    }

    public Map<String, Float> getProperties() {
        return Collections.unmodifiableMap(mProperties);
    }

}
