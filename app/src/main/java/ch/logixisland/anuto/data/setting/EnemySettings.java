package ch.logixisland.anuto.data.setting;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import ch.logixisland.anuto.data.serializer.SerializerFactory;

@Root
public class EnemySettings {

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

}
