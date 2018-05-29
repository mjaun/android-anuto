package ch.logixisland.anuto.data.state;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.HashMap;
import java.util.Map;

public class TowerData extends EntityData {

    @Element(name = "plateauId")
    private int mPlateauId;

    @Element(name = "value")
    private int mValue;

    @Element(name = "level")
    private int mLevel;

    @Element(name = "damageInflicted")
    private float mDamageInflicted;

    @Element(name = "strategy", required = false)
    private String mStrategy;

    @Element(name = "lockTarget")
    private boolean mLockTarget;

    @ElementMap(name = "details", attribute = true)
    private Map<String, String> mDetails = new HashMap<>();

    public int getPlateauId() {
        return mPlateauId;
    }

    public void setPlateauId(int plateauId) {
        mPlateauId = plateauId;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public float getDamageInflicted() {
        return mDamageInflicted;
    }

    public void setDamageInflicted(float damageInflicted) {
        mDamageInflicted = damageInflicted;
    }

    public String getStrategy() {
        return mStrategy;
    }

    public void setStrategy(String strategy) {
        mStrategy = strategy;
    }

    public boolean isLockTarget() {
        return mLockTarget;
    }

    public void setLockTarget(boolean lockTarget) {
        mLockTarget = lockTarget;
    }

    public String getDetail(String key) {
        return mDetails.get(key);
    }

    public void addDetail(String key, String value) {
        mDetails.put(key, value);
    }
}
