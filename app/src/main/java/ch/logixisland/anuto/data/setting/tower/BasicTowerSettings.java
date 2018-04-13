package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import ch.logixisland.anuto.data.setting.enemy.WeaponType;

@Root
public class BasicTowerSettings {

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

    @Element(name = "upgrade", required = false)
    private String mUpgrade;

    @Element(name = "upgradeCost", required = false)
    private int mUpgradeCost;

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

    public String getUpgrade() {
        return mUpgrade;
    }

    public int getUpgradeCost() {
        return mUpgradeCost;
    }
}
