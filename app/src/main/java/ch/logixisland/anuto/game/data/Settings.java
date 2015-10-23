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
    public float difficultyOffset;

    @Element
    public float difficultyLinear;

    @Element
    public float difficultyQuadratic;

    @Element
    public float rewardModifier;

    @Element
    public float rewardRoot;

    @Element
    public float minSpeedModifier;

    @Element
    public float weakAgainstModifier;

    @Element
    public float strongAgainstModifier;
}
