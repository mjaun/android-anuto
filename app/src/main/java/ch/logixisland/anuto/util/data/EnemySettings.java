package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@Root
public class EnemySettings {

    @Element(name = "minSpeedModifier")
    private float mMinSpeedModifier;

    @Element(name = "weakAgainstModifier")
    private float mWeakAgainstModifier;

    @Element(name = "strongAgainstModifier")
    private float mStrongAgainstModifier;

    @ElementList(entry = "enemy", inline = true)
    private Collection<EnemyConfig> mEnemyConfigs = new ArrayList<>();

    public static EnemySettings fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(EnemySettings.class, stream);
    }

    public EnemyConfig getEnemyConfig(String name) {
        for (EnemyConfig config : mEnemyConfigs) {
            if (config.getName().equals(name)) {
                return config;
            }
        }

        return null;
    }

    public float getMinSpeedModifier() {
        return mMinSpeedModifier;
    }

    public float getWeakAgainstModifier() {
        return mWeakAgainstModifier;
    }

    public float getStrongAgainstModifier() {
        return mStrongAgainstModifier;
    }
}
