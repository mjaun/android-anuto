package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MortarSettings extends BasicTowerSettings {

    @Element(name = "inaccuracy")
    private float mInaccuracy;

    @Element(name = "explosionRadius")
    private float mExplosionRadius;

    @Element(name = "enhanceExplosionRadius")
    private float mEnhanceExplosionRadius;

    public float getInaccuracy() {
        return mInaccuracy;
    }

    public float getExplosionRadius() {
        return mExplosionRadius;
    }

    public float getEnhanceExplosionRadius() {
        return mEnhanceExplosionRadius;
    }

}
