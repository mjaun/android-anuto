package ch.logixisland.anuto.data.wave;

import android.content.Context;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.SerializerFactory;

@Root
public class WaveDescriptorList {

    @ElementList(inline = true, entry = "wave")
    private List<WaveDescriptor> mWaves = new ArrayList<>();

    private WaveDescriptorList() {
    }

    public static List<WaveDescriptor> fromXml(Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return fromXml(stream);
        } finally {
            stream.close();
        }
    }

    private static List<WaveDescriptor> fromXml(InputStream inputStream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(WaveDescriptorList.class, inputStream).mWaves;
    }

}
