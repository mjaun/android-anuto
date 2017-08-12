package ch.logixisland.anuto.data.setting.game;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.SerializerFactory;

@Root
public class GameSettingsRoot {

    @Element(name = "credits")
    private int mCredits;

    @Element(name = "lives")
    private int mLives;

    @Element(name = "difficultyModifier")
    private float mDifficultyModifier;

    @Element(name = "difficultyExponent")
    private float mDifficultyExponent;

    @Element(name = "difficultyLinear")
    private float mDifficultyLinear;

    @Element(name = "minHealthModifier")
    private float mMinHealthModifier;

    @Element(name = "rewardModifier")
    private float mRewardModifier;

    @Element(name = "rewardExponent")
    private float mRewardExponent;

    @Element(name = "minRewardModifier")
    private float mMinRewardModifier;

    @Element(name = "earlyModifier")
    private float mEarlyModifier;

    @Element(name = "earlyExponent")
    private float mEarlyExponent;

    @Element(name = "ageModifier")
    private float mAgeModifier;

    public static GameSettingsRoot fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(GameSettingsRoot.class, stream);
    }

    public int getCredits() {
        return mCredits;
    }

    public int getLives() {
        return mLives;
    }

    public float getDifficultyModifier() {
        return mDifficultyModifier;
    }

    public float getDifficultyExponent() {
        return mDifficultyExponent;
    }

    public float getDifficultyLinear() {
        return mDifficultyLinear;
    }

    public float getMinHealthModifier() {
        return mMinHealthModifier;
    }

    public float getRewardModifier() {
        return mRewardModifier;
    }

    public float getRewardExponent() {
        return mRewardExponent;
    }

    public float getMinRewardModifier() {
        return mMinRewardModifier;
    }

    public float getEarlyModifier() {
        return mEarlyModifier;
    }

    public float getEarlyExponent() {
        return mEarlyExponent;
    }

    public float getAgeModifier() {
        return mAgeModifier;
    }
}
