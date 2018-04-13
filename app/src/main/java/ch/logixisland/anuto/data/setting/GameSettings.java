package ch.logixisland.anuto.data.setting;

import android.content.Context;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.SerializerFactory;
import ch.logixisland.anuto.data.setting.enemy.EnemySettings;
import ch.logixisland.anuto.data.setting.tower.TowerSettings;

@Root
public class GameSettings {

    @Element(name = "credits")
    private int mCredits;

    @Element(name = "lives")
    private int mLives;

    @Element(name = "tickCount", required = false)
    private int mTickCount;

    @Element(name = "nextEntityId", required = false)
    private int mNextEntityId;

    @Element(name = "waveNumber", required = false)
    private int mWaveNumber;

    @Element(name = "creditsEarned", required = false)
    private int mCreditsEarned;

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

    @Element(name = "enemySettings", required = false)
    private EnemySettings mEnemySettings;

    @Element(name = "towerSettings", required = false)
    private TowerSettings mTowerSettings;

    // TODO remove
    public static GameSettings fromXmlOld(Context context, int resId) throws Exception {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return fromXml(stream);
        } finally {
            stream.close();
        }
    }

    public static GameSettings fromXml(Context context, int gameSettingsResId, int enemySettingsResId,
                                       int towerSettingsResId) throws Exception {
        InputStream stream = context.getResources().openRawResource(gameSettingsResId);
        GameSettings settings;

        try {
            settings = fromXml(stream);
            settings.mEnemySettings = EnemySettings.fromXml(context, enemySettingsResId);
            settings.mTowerSettings = TowerSettings.fromXml(context, towerSettingsResId);
        } finally {
            stream.close();
        }

        return settings;
    }

    private static GameSettings fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(GameSettings.class, stream);
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

}
