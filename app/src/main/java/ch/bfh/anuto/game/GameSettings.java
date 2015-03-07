package ch.bfh.anuto.game;

import org.simpleframework.xml.Element;

public class GameSettings {
    @Element
    public int width = 10;

    @Element
    public int height = 15;
}
