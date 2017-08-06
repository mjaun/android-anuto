package ch.logixisland.anuto.data.enemy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.serializer.SerializerFactory;

@Root
public class EnemySettingsRoot {

    @Element(name = "global")
    private EnemyGlobalSettings mGlobalSettings;

    @Element(name = "soldier")
    private EnemySettings mSoldierSettings;

    @Element(name = "blob")
    private EnemySettings mBlobSettings;

    @Element(name = "sprinter")
    private EnemySettings mSprinterSettings;

    @Element(name = "healer")
    private HealerSettings mHealerSettings;

    @Element(name = "flyer")
    private EnemySettings mFlyerSettings;

    public static EnemySettingsRoot fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(EnemySettingsRoot.class, stream);
    }

    public EnemyGlobalSettings getGlobalSettings() {
        return mGlobalSettings;
    }

    public EnemySettings getSoldierSettings() {
        return mSoldierSettings;
    }

    public EnemySettings getBlobSettings() {
        return mBlobSettings;
    }

    public EnemySettings getSprinterSettings() {
        return mSprinterSettings;
    }

    public HealerSettings getHealerSettings() {
        return mHealerSettings;
    }

    public EnemySettings getFlyerSettings() {
        return mFlyerSettings;
    }
}
