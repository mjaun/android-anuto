package ch.logixisland.anuto.data.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MineLayerSettings extends TowerSettings {

    @Element(name = "maxMineCount")
    private int mMaxMineCount;

    @Element(name = "enhanceMaxMineCount")
    private int mEnhanceMaxMineCount;

    @Element(name = "explosionRadius")
    private float mExplosionRadius;

    @Element(name = "enhanceExplosionRadius")
    private float mEnhanceExplosionRadius;

    public int getMaxMineCount() {
        return mMaxMineCount;
    }

    public int getEnhanceMaxMineCount() {
        return mEnhanceMaxMineCount;
    }

    public float getExplosionRadius() {
        return mExplosionRadius;
    }

    public float getEnhanceExplosionRadius() {
        return mEnhanceExplosionRadius;
    }

}
