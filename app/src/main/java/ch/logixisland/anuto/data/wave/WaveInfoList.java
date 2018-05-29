package ch.logixisland.anuto.data.wave;

import android.content.res.Resources;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Root
public final class WaveInfoList {

    @ElementList(inline = true, entry = "wave")
    private List<WaveInfo> mWaves = new ArrayList<>();

    private WaveInfoList() {
    }

    public static List<WaveInfo> fromXml(Serializer serializer, Resources resources, int resId) throws Exception {
        InputStream stream = resources.openRawResource(resId);

        try {
            return serializer.read(WaveInfoList.class, stream).mWaves;
        } finally {
            stream.close();
        }
    }

}
