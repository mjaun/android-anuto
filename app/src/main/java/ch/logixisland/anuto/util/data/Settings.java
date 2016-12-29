package ch.logixisland.anuto.util.data;

import org.simpleframework.xml.Element;

public class Settings {

    /*
    ------ Fields ------
     */

    @Element
    private int width;

    @Element
    private int height;

    @Element
    private int credits;

    @Element
    private int lives;

    @Element
    private float ageModifier;

    @Element
    private float earlyModifier;

    @Element
    private float earlyRoot;

    @Element
    private boolean endless;

    @Element
    private float difficultyOffset;

    @Element
    private float difficultyLinear;

    @Element
    private float difficultyQuadratic;

    @Element
    private float rewardModifier;

    @Element
    private float rewardRoot;

    @Element
    private float minSpeedModifier;

    @Element
    private float weakAgainstModifier;

    @Element
    private float strongAgainstModifier;

    /*
    ------ Methods ------
     */

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCredits() {
        return credits;
    }

    public int getLives() {
        return lives;
    }

    public float getAgeModifier() {
        return ageModifier;
    }

    public float getEarlyModifier() {
        return earlyModifier;
    }

    public float getEarlyRoot() {
        return earlyRoot;
    }

    public boolean isEndless() {
        return endless;
    }

    public float getDifficultyOffset() {
        return difficultyOffset;
    }

    public float getDifficultyLinear() {
        return difficultyLinear;
    }

    public float getDifficultyQuadratic() {
        return difficultyQuadratic;
    }

    public float getRewardModifier() {
        return rewardModifier;
    }

    public float getRewardRoot() {
        return rewardRoot;
    }

    public float getMinSpeedModifier() {
        return minSpeedModifier;
    }

    public float getWeakAgainstModifier() {
        return weakAgainstModifier;
    }

    public float getStrongAgainstModifier() {
        return strongAgainstModifier;
    }
}
