package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import ch.logixisland.anuto.util.math.Vector2;

@Root
public class EntityDescriptor {

    @Element(name = "name")
    private String mName;

    @Element(name = "position")
    private Vector2 mPosition;

}
