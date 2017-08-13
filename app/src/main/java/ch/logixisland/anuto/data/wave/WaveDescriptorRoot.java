package ch.logixisland.anuto.data.wave;

import android.content.Context;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.data.SerializerFactory;

@Root
public class WaveDescriptorRoot {

    @ElementList(inline = true, entry = "wave")
    private List<WaveDescriptor> mWaves = new ArrayList<>();

    public static WaveDescriptorRoot fromXml(Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return fromXml(stream);
        } finally {
            stream.close();
        }
    }

    public static WaveDescriptorRoot fromXml(InputStream inputStream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(WaveDescriptorRoot.class, inputStream);
    }

    public List<WaveDescriptor> getWaves() {
        return Collections.unmodifiableList(mWaves);
    }

}
