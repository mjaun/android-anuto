package ch.logixisland.anuto.data.setting.tower;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.SerializerFactory;

@Root
public class TowerSettingsRoot {

    @Element(name = "ageModifier")
    private float mAgeModifier;

    @Element(name = "slots")
    private TowerSlots mTowerSlots;

    @Element(name = "canon")
    private TowerSettings mCanonSettings;

    @Element(name = "dualCanon")
    private TowerSettings mDualCanonSettings;

    @Element(name = "machineGun")
    private TowerSettings mMachineGunSettings;

    @Element(name = "simpleLaser")
    private TowerSettings mSimpleLaserSettings;

    @Element(name = "bouncingLaser")
    private BouncingLaserSettings mBouncingLaserSettings;

    @Element(name = "straightLaser")
    private TowerSettings mStraightLaserSettings;

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


    public static TowerSettingsRoot fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(TowerSettingsRoot.class, stream);
    }

    public float getAgeModifier() {
        return mAgeModifier;
    }

    public TowerSlots getTowerSlots() {
        return mTowerSlots;
    }

    public TowerSettings getCanonSettings() {
        return mCanonSettings;
    }

    public TowerSettings getDualCanonSettings() {
        return mDualCanonSettings;
    }

    public TowerSettings getMachineGunSettings() {
        return mMachineGunSettings;
    }

    public TowerSettings getSimpleLaserSettings() {
        return mSimpleLaserSettings;
    }

    public BouncingLaserSettings getBouncingLaserSettings() {
        return mBouncingLaserSettings;
    }

    public TowerSettings getStraightLaserSettings() {
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
