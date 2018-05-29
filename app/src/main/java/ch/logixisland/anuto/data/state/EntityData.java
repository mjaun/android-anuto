package ch.logixisland.anuto.data.state;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import ch.logixisland.anuto.util.math.Vector2;

@Root
public abstract class EntityData {

    @Element(name = "id")
    private int mId;

    @Element(name = "entityName")
    private String mName;

    @Element(name = "position")
    private Vector2 mPosition;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Vector2 getPosition() {
        return mPosition;
    }

    public void setPosition(Vector2 position) {
        mPosition = position;
    }

}
