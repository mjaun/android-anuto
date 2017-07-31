package ch.logixisland.anuto.util.data;

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
public class LevelDescriptor {

    @Element(name = "width")
    private int mWidth;

    @Element(name = "height")
    private int mHeight;

    @ElementList(name = "plateaus", entry = "plateau")
    private List<PlateauDescriptor> mPlateaus = new ArrayList<>();

    @ElementList(name = "paths", entry = "path")
    private List<PathDescriptor> mPaths = new ArrayList<>();

    public static LevelDescriptor fromXml(InputStream inputStream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(LevelDescriptor.class, inputStream);
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
