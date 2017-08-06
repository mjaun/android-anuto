package ch.logixisland.anuto.data.setting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.serializer.SerializerFactory;

@Root
public class EnemySettings {

    @Element(name = "soldier")
    private EnemyProperties mSoldierProperties;

    @Element(name = "blob")
    private EnemyProperties mBlobProperties;

    @Element(name = "sprinter")
    private EnemyProperties mSprinterProperties;

    @Element(name = "healer")
    private EnemyProperties mHealerProperties;

    @Element(name = "flyer")
    private EnemyProperties mFlyerProperties;

    public static EnemySettings fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(EnemySettings.class, stream);
    }

    public EnemyProperties getSoldierProperties() {
        return mSoldierProperties;
    }

    public EnemyProperties getBlobProperties() {
        return mBlobProperties;
    }

    public EnemyProperties getSprinterProperties() {
        return mSprinterProperties;
    }

    public EnemyProperties getHealerProperties() {
        return mHealerProperties;
    }

    public EnemyProperties getFlyerProperties() {
        return mFlyerProperties;
    }
}
