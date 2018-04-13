package ch.logixisland.anuto.data.setting.tower;

import android.content.Context;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.SerializerFactory;

@Root
public class TowerSettings {

    @Element(name = "ageModifier")
    private float mAgeModifier;

    @Element(name = "slots")
    private TowerSlots mTowerSlots;

    @Element(name = "canon")
    private BasicTowerSettings mCanonSettings;

    @Element(name = "dualCanon")
    private BasicTowerSettings mDualCanonSettings;

    @Element(name = "machineGun")
    private BasicTowerSettings mMachineGunSettings;

    @Element(name = "simpleLaser")
    private BasicTowerSettings mSimpleLaserSettings;

    @Element(name = "bouncingLaser")
    private BouncingLaserSettings mBouncingLaserSettings;

    @Element(name = "straightLaser")
    private BasicTowerSettings mStraightLaserSettings;

    @Element(name = "mortar")
    private MortarSettings mMortarSettings;

    @Element(name = "mineLayer")
    private MineLayerSettings mMineLayerSettings;

    @Element(name = "rocketLauncher")
    private RocketLauncherSettings mRocketLauncherSettings;

    @Element(name = "glueTower")
    private GlueTowerSettings mGlueTowerSettings;

    @Element(name = "glueGun")
    private GlueGunSettings mGlueGunSettings;

    @Element(name = "teleporter")
    private TeleporterSettings mTeleporterSettings;

    public static TowerSettings fromXml(Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return fromXml(stream);
        } finally {
            stream.close();
        }
    }

    public static TowerSettings fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(TowerSettings.class, stream);
    }

    public float getAgeModifier() {
        return mAgeModifier;
    }

    public TowerSlots getTowerSlots() {
        return mTowerSlots;
    }

    public BasicTowerSettings getCanonSettings() {
        return mCanonSettings;
    }

    public BasicTowerSettings getDualCanonSettings() {
        return mDualCanonSettings;
    }

    public BasicTowerSettings getMachineGunSettings() {
        return mMachineGunSettings;
    }

    public BasicTowerSettings getSimpleLaserSettings() {
        return mSimpleLaserSettings;
    }

    public BouncingLaserSettings getBouncingLaserSettings() {
        return mBouncingLaserSettings;
    }

    public BasicTowerSettings getStraightLaserSettings() {
        return mStraightLaserSettings;
    }

    public MortarSettings getMortarSettings() {
        return mMortarSettings;
    }

    public MineLayerSettings getMineLayerSettings() {
        return mMineLayerSettings;
    }

    public RocketLauncherSettings getRocketLauncherSettings() {
        return mRocketLauncherSettings;
    }

    public GlueTowerSettings getGlueTowerSettings() {
        return mGlueTowerSettings;
    }

    public GlueGunSettings getGlueGunSettings() {
        return mGlueGunSettings;
    }

    public TeleporterSettings getTeleporterSettings() {
        return mTeleporterSettings;
    }
}
