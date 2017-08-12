package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;

public class TowerDescriptor extends EntityDescriptor {

    @Element(name = "value")
    private int mValue;

    @Element(name = "level")
    private int mLevel;

}
