package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class EnemyDescriptor {

    @Attribute(name = "name")
    private String mName;

    @Attribute(name = "pathIndex", required = false)
    private int mPathIndex;

    @Attribute(name = "delay", required = false)
    private float mDelay;

    @Attribute(name = "offset", required = false)
    private float mOffset;

    public String getName() {
        return mName;
    }

    public int getPathIndex() {
        return mPathIndex;
    }

    public float getDelay() {
        return mDelay;
    }

    public float getOffset() {
        return mOffset;
    }
}
