package ch.logixisland.anuto.data.setting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.serializer.SerializerFactory;

@Root
public class EnemySettingsRoot {

    @Element(name = "soldier")
    private EnemySettings mSoldierProperties;

    @Element(name = "blob")
    private EnemySettings mBlobProperties;

    @Element(name = "sprinter")
    private EnemySettings mSprinterProperties;

    @Element(name = "healer")
    private HealerSettings mHealerProperties;

    @Element(name = "flyer")
    private EnemySettings mFlyerProperties;

    public static EnemySettingsRoot fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(EnemySettingsRoot.class, stream);
    }

    public EnemySettings getSoldierProperties() {
        return mSoldierProperties;
    }

    public EnemySettings getBlobProperties() {
        return mBlobProperties;
    }

    public EnemySettings getSprinterProperties() {
        return mSprinterProperties;
    }

    public HealerSettings getHealerProperties() {
        return mHealerProperties;
    }

    public EnemySettings getFlyerProperties() {
        return mFlyerProperties;
    }
}
