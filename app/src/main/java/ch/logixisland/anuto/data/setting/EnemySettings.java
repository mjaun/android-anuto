package ch.logixisland.anuto.data.setting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Root
public class EnemySettings {

    @Element(name = "health")
    private float mHealth;

    @Element(name = "speed")
    private float mSpeed;

    @Element(name = "reward")
    private int mReward;

    @ElementList(entry = "weakAgainst", inline = true, required = false)
    private Collection<WeaponType> mWeakAgainst = new ArrayList<>();

    @ElementList(entry = "strongAgainst", inline = true, required = false)
    private Collection<WeaponType> mStrongAgainst = new ArrayList<>();

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
        return Collections.unmodifiableCollection(mWeakAgainst);
    }

    public Collection<WeaponType> getStrongAgainst() {
        return Collections.unmodifiableCollection(mStrongAgainst);
    }

}
