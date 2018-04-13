package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class BouncingLaserSettings extends BasicTowerSettings {

    @Element(name = "bounceCount")
    private int mBounceCount;

    @Element(name = "bounceDistance")
    private float mBounceDistance;

    public int getBounceCount() {
        return mBounceCount;
    }

    public float getBounceDistance() {
        return mBounceDistance;
    }

}
