package ch.logixisland.anuto.data.map;

import android.content.Context;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Root
public class MapDescriptor {

    @Element(name = "width")
    private int mWidth;

    @Element(name = "height")
    private int mHeight;

    @ElementList(name = "plateaus", entry = "plateau")
    private List<PlateauDescriptor> mPlateaus = new ArrayList<>();

    @ElementList(name = "paths", entry = "path")
    private List<PathDescriptor> mPaths = new ArrayList<>();

    public static MapDescriptor fromXml(Serializer serializer, Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return serializer.read(MapDescriptor.class, stream);
        } finally {
            stream.close();
        }
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public Collection<PlateauDescriptor> getPlateaus() {
        return Collections.unmodifiableCollection(mPlateaus);
    }

    public List<PathDescriptor> getPaths() {
        return Collections.unmodifiableList(mPaths);
    }

}
