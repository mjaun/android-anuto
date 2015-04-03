package ch.bfh.anuto.game.data;

import org.simpleframework.xml.Element;

public class GameSettings {
    @Element
    public int width = 10;

    @Element
    public int height = 15;

    @Element
    public int credits = 200;

    @Element
    public int lives = 20;
}
