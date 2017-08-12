package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class RocketLauncherSettings extends TowerSettings {

    @Element(name = "explosionRadius")
    private float mExplosionRadius;

    @Element(name = "enhanceExplosionRadius")
    private float mEnhanceExplosionRadius;

    public float getExplosionRadius() {
        return mExplosionRadius;
    }

    public float getEnhanceExplosionRadius() {
        return mEnhanceExplosionRadius;
    }

}
