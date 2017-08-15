package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;

public class TowerDescriptor extends EntityDescriptor {

    @Element(name = "value")
    private int mValue;

    @Element(name = "level")
    private int mLevel;

    @Element(name = "damageInflicted")
    private float mDamageInflicted;

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
}
