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
public class TowerSettingsRoot {

    @ElementList(entry = "tower", inline = true)
    private Collection<TowerSettings> mTowerSettings = new ArrayList<>();

    public static TowerSettingsRoot fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(TowerSettingsRoot.class, stream);
    }

    public TowerSettings getTowerConfig(String name) {
        for (TowerSettings config : mTowerSettings) {
            if (config.getName().equals(name)) {
                return config;
            }
        }

        return null;
    }

    public Collection<TowerSettings> getTowerSettings() {
        return Collections.unmodifiableCollection(mTowerSettings);
    }

}
