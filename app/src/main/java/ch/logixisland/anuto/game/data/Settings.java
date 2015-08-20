package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;

public class Settings {
    @Element
    public int width = 10;

    @Element
    public int height = 15;

    @Element
    public int credits = 800;

    @Element
    public int lives = 5;

    @Element
    public float agingFactor = 0.9f;

    @Element
    public float earlyFactor = 1.0f;
}
