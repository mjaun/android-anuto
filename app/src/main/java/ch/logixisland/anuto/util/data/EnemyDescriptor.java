package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.util.math.vector.Vector2;

public class EnemyDescriptor {

    @Attribute(name="name")
    private String mName;

    @Attribute(required=false)
    private int mPathIndex;

    @Attribute(required=false)
    private float mDelay;

    @Attribute(required=false)
    private float mOffsetX;

    @Attribute(required=false)
    private float mOffsetY;

    public String getName() {
        return mName;
    }

    public int getPathIndex() {
        return mPathIndex;
    }

    public float getDelay() {
        return mDelay;
    }

    public Vector2 getOffset() {
        return new Vector2(mOffsetX, mOffsetY);
    }
}
