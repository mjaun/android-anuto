package ch.logixisland.anuto.data.state;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.util.math.Vector2;

public class EnemyData extends EntityData {

    @Element(name = "waveNumber")
    private int mWaveNumber;

    @Element(name = "reward")
    private int mReward;

    @Element(name = "health")
    private float mHealth;

    @Element(name = "maxHealth")
    private float mMaxHealth;

    @ElementList(name = "wayPoints", entry = "wayPoint")
    private List<Vector2> mWayPoints = new ArrayList<>();

    @Element(name = "wayPointIndex")
    private int mWayPointIndex;

    public int getWaveNumber() {
        return mWaveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        mWaveNumber = waveNumber;
    }

    public int getReward() {
        return mReward;
    }

    public void setReward(int reward) {
        mReward = reward;
    }

    public float getHealth() {
        return mHealth;
    }

    public void setHealth(float health) {
        mHealth = health;
    }

    public float getMaxHealth() {
        return mMaxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        mMaxHealth = maxHealth;
    }

    public List<Vector2> getWayPoints() {
        return mWayPoints;
    }

    public void setWayPoints(List<Vector2> wayPoints) {
        mWayPoints = wayPoints;
    }

    public int getWayPointIndex() {
        return mWayPointIndex;
    }

    public void setWayPointIndex(int wayPointIndex) {
        mWayPointIndex = wayPointIndex;
    }
}
