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
    public float ageModifier;

    @Element
    public float earlyModifier;

    @Element
    public float earlyRoot;

    @Element
    public boolean endless;

    @Element
    public float linearDifficulty;

    @Element
    public float quadraticDifficulty;

    @Element
    public float rewardModifier;

    @Element
    public float rewardRoot;

    @Element
    public float minSpeedModifier;
}
