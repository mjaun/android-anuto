package ch.logixisland.anuto.data.enemy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class EnemyGlobalSettings {

    @Element(name = "minSpeedModifier")
    private float mMinSpeedModifier;

    @Element(name = "weakAgainstModifier")
    private float mWeakAgainstModifier;

    @Element(name = "strongAgainstModifier")
    private float mStrongAgainstModifier;

    public float getMinSpeedModifier() {
        return mMinSpeedModifier;
    }

    public float getWeakAgainstModifier() {
        return mWeakAgainstModifier;
    }

    public float getStrongAgainstModifier() {
        return mStrongAgainstModifier;
    }

}
