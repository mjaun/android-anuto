package ch.logixisland.anuto.data.setting;

import org.simpleframework.xml.Element;

public class HealerSettings extends EnemySettings {

    @Element(name = "healAmount")
    private float mHealAmount;

    @Element(name = "healRadius")
    private float mHealRadius;

    @Element(name = "healInterval")
    private float mHealInterval;

    @Element(name = "healDuration")
    private float mHealDuration;

    public float getHealAmount() {
        return mHealAmount;
    }

    public float getHealRadius() {
        return mHealRadius;
    }

    public float getHealInterval() {
        return mHealInterval;
    }

    public float getHealDuration() {
        return mHealDuration;
    }

}
