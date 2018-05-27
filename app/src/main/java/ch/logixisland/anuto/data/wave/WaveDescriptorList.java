package ch.logixisland.anuto.data.wave;

import android.content.Context;

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

    public static List<WaveDescriptor> fromXml(Serializer serializer, Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return serializer.read(WaveDescriptorList.class, stream).mWaves;
        } finally {
            stream.close();
        }
    }

}
