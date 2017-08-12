package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class GlueGunSettings extends TowerSettings {

    @Element(name = "glueIntensity")
    private float mGlueIntensity;

    @Element(name = "enhanceGlueIntensity")
    private float mEnhanceGlueIntensity;

    @Element(name = "glueDuration")
    private float mGlueDuration;

    public float getGlueIntensity() {
        return mGlueIntensity;
    }

    public float getEnhanceGlueIntensity() {
        return mEnhanceGlueIntensity;
    }

    public float getGlueDuration() {
        return mGlueDuration;
    }

}
