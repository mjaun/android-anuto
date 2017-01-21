package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.HashMap;
import java.util.Map;

public class TowerConfig {

    @Element(name = "slot", required = false)
    private int mSlot = -1;

    @Element(name = "name")
    private String mName;

    @Element(name = "upgrade", required = false)
    private String mUpgrade;

    private int mUpgradeCost;

    @Element(name = "value")
    private int mValue;

    @Element(name = "damage", required = false)
    private float mDamage;

    @Element(name = "range")
    private float mRange;

    @Element(name = "reload")
    private float mReload;

    @Element(name = "maxLevel")
    private int mMaxLevel;

    @Element(name = "weaponType", required = false)
    private WeaponType mWeaponType;

    @Element(name = "enhanceBase")
    private float mEnhanceBase;

    @Element(name = "enhanceCost")
    private int mEnhanceCost;

    @Element(name = "enhanceDamage", required = false)
    private float mEnhanceDamage;

    @Element(name = "enhanceRange", required = false)
    private float mEnhanceRange;

    @Element(name = "enhanceReload", required = false)
    private float mEnhanceReload;

    @ElementMap(entry = "property", key = "name", inline = true, required = false, attribute = true)
    private Map<String, Float> mProperties = new HashMap<>();

    public int getSlot() {
        return mSlot;
    }

    public String getName() {
        return mName;
    }

    public String getUpgrade() {
        return mUpgrade;
    }

    public int getUpgradeCost() {
        return mUpgradeCost;
    }

    public void setUpgradeCost(int upgradeCost) {
        mUpgradeCost = upgradeCost;
    }

    public int getValue() {
        return mValue;
    }

    public float getDamage() {
        return mDamage;
    }

    public float getRange() {
        return mRange;
    }

    public float getReload() {
        return mReload;
    }

    public int getMaxLevel() {
        return mMaxLevel;
    }

    public WeaponType getWeaponType() {
        return mWeaponType;
    }

    public float getEnhanceBase() {
        return mEnhanceBase;
    }

    public int getEnhanceCost() {
        return mEnhanceCost;
    }

    public float getEnhanceDamage() {
        return mEnhanceDamage;
    }

    public float getEnhanceRange() {
        return mEnhanceRange;
    }

    public float getEnhanceReload() {
        return mEnhanceReload;
    }

    public Map<String, Float> getProperties() {
        return mProperties;
    }
}
