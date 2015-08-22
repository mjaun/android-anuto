package ch.logixisland.anuto.game.data;

import org.simpleframework.xml.Element;

public class Settings {
    @Element
    public int width;

    @Element
    public int height;

    @Element
    public int credits;

    @Element
    public int lives;

    @Element
    public float agingFactor;

    @Element
    public float earlyFactor;

    @Element(required=false)
    public boolean endless;

    @Element(required=false)
    public float linearDifficulty;

    @Element(required=false)
    public float quadraticDifficulty;
}
