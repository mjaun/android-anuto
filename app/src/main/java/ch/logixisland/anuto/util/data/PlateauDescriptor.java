package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Attribute;

import ch.logixisland.anuto.util.math.vector.Vector2;

public class PlateauDescriptor {

    @Attribute(name = "name")
    private String mName;

    @Attribute(name = "x")
    private float mX;

    @Attribute(name = "y")
    private float mY;

    public String getName() {
        return mName;
    }

    public Vector2 getPosition() {
        return new Vector2(mX, mY);
    }

}
