package ch.logixisland.anuto.data.setting;

import android.content.res.Resources;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;

import ch.logixisland.anuto.data.setting.enemy.EnemySettings;
import ch.logixisland.anuto.data.setting.tower.TowerSettings;

@Root
public class GameSettings {

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

    @Element(name = "minSpeedModifier")
    private float mMinSpeedModifier;

    @Element(name = "weakAgainstModifier")
    private float mWeakAgainstModifier;

    @Element(name = "strongAgainstModifier")
    private float mStrongAgainstModifier;

    @Element(name = "enemySettings", required = false)
    private EnemySettings mEnemySettings;

    @Element(name = "towerSettings", required = false)
    private TowerSettings mTowerSettings;

    public static GameSettings fromXml(Serializer serializer, Resources resources, int gameSettingsResId,
                                       int enemySettingsResId, int towerSettingsResId) throws Exception {
        InputStream stream = resources.openRawResource(gameSettingsResId);
        GameSettings settings;

        try {
            settings = serializer.read(GameSettings.class, stream);
            settings.mEnemySettings = EnemySettings.fromXml(serializer, resources, enemySettingsResId);
            settings.mTowerSettings = TowerSettings.fromXml(serializer, resources, towerSettingsResId);
        } finally {
            stream.close();
        }

        return settings;
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

    public float getMinSpeedModifier() {
        return mMinSpeedModifier;
    }

    public float getWeakAgainstModifier() {
        return mWeakAgainstModifier;
    }

    public float getStrongAgainstModifier() {
        return mStrongAgainstModifier;
    }

    public EnemySettings getEnemySettings() {
        return mEnemySettings;
    }

    public TowerSettings getTowerSettings() {
        return mTowerSettings;
    }
}
