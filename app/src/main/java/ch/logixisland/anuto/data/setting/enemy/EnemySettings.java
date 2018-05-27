package ch.logixisland.anuto.data.setting.enemy;

import android.content.res.Resources;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

@Root
public class EnemySettings {

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

    public static EnemySettings fromXml(Serializer serializer, Resources resources, int resId) throws Exception {
        InputStream stream = resources.openRawResource(resId);

        try {
            return serializer.read(EnemySettings.class, stream);
        } finally {
            stream.close();
        }
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
