package ch.logixisland.anuto.data.wave;

import android.content.res.Resources;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Root
public class WaveDescriptorList {

    @ElementList(inline = true, entry = "wave")
    private List<WaveDescriptor> mWaves = new ArrayList<>();

    private WaveDescriptorList() {
    }

    public static List<WaveDescriptor> fromXml(Serializer serializer, Resources resources, int resId) throws Exception {
        InputStream stream = resources.openRawResource(resId);

        try {
            return serializer.read(WaveDescriptorList.class, stream).mWaves;
        } finally {
            stream.close();
        }
    }

}
