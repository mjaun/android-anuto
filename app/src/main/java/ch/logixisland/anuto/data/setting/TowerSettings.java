package ch.logixisland.anuto.data.setting;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ch.logixisland.anuto.data.serializer.SerializerFactory;

@Root
public class TowerSettings {

    @ElementList(entry = "tower", inline = true)
    private Collection<TowerConfig> mTowerConfigs = new ArrayList<>();

    public static TowerSettings fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(TowerSettings.class, stream);
    }

    public TowerConfig getTowerConfig(String name) {
        for (TowerConfig config : mTowerConfigs) {
            if (config.getName().equals(name)) {
                return config;
            }
        }

        return null;
    }

    public Collection<TowerConfig> getTowerConfigs() {
        return Collections.unmodifiableCollection(mTowerConfigs);
    }

}
