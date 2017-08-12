package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TeleporterSettings extends TowerSettings {

    @Element(name = "teleportDistance")
    private float mTeleportDistance;

    @Element(name = "enhanceTeleportDistance")
    private float mEnhanceTeleportDistance;

    public float getTeleportDistance() {
        return mTeleportDistance;
    }

    public float getEnhanceTeleportDistance() {
        return mEnhanceTeleportDistance;
    }
}
