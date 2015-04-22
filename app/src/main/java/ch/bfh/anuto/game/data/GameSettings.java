package ch.bfh.anuto.game.data;

import org.simpleframework.xml.Element;

public class GameSettings {
    @Element
    public int width;

    @Element
    public int height;

    @Element
    public int credits;

    @Element
    public int lives;

    @Element(required=false)
    public float agingFactor = 0.95f;

    @Element(required=false)
    public float earlyBonusFactor = 2.0f;

    @Element(required=false)
    public float earlyBonusDecreaseFactor = 0.7f;
}
