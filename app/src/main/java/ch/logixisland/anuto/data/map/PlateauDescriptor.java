package ch.logixisland.anuto.data.map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import ch.logixisland.anuto.util.math.Vector2;

@Root
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
