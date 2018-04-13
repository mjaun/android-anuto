package ch.logixisland.anuto.data.setting.enemy;

import android.content.Context;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.SerializerFactory;

@Root
public class EnemySettings {

    @Element(name = "global")
    private GlobalSettings mGlobalSettings;

    @Element(name = "soldier")
    private BasicEnemySettings mSoldierSettings;

    @Element(name = "blob")
    private BasicEnemySettings mBlobSettings;

    @Element(name = "sprinter")
    private BasicEnemySettings mSprinterSettings;

    @Element(name = "healer")
    private HealerSettings mHealerSettings;

    @Element(name = "flyer")
    private BasicEnemySettings mFlyerSettings;

    public static EnemySettings fromXml(Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return fromXml(stream);
        } finally {
            stream.close();
        }
    }

    private static EnemySettings fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(EnemySettings.class, stream);
    }

    public GlobalSettings getGlobalSettings() {
        return mGlobalSettings;
    }

    public BasicEnemySettings getSoldierSettings() {
        return mSoldierSettings;
    }

    public BasicEnemySettings getBlobSettings() {
        return mBlobSettings;
    }

    public BasicEnemySettings getSprinterSettings() {
        return mSprinterSettings;
    }

    public HealerSettings getHealerSettings() {
        return mHealerSettings;
    }

    public BasicEnemySettings getFlyerSettings() {
        return mFlyerSettings;
    }
}
