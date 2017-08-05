package ch.logixisland.anuto.data.descriptor;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.data.serializer.SerializerFactory;

public class WavesDescriptor {

    @ElementList(inline = true, entry = "wave")
    private List<WaveDescriptor> mWaves = new ArrayList<>();

    public static WavesDescriptor fromXml(InputStream inputStream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(WavesDescriptor.class, inputStream);
    }

    public List<WaveDescriptor> getWaves() {
        return Collections.unmodifiableList(mWaves);
    }

}
