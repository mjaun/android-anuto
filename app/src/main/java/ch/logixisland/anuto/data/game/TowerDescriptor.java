package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;

import ch.logixisland.anuto.util.math.Vector2;

public class TowerDescriptor {

    @Element(name = "name")
    private String mName;

    @Element(name = "position")
    private Vector2 mPosition;

    @Element(name = "value")
    private int mValue;

    @Element(name = "level")
    private int mLevel;

}
