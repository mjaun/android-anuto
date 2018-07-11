package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.enemy.WeaponType;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.util.container.KeyValueStore;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public abstract class Tower extends Entity {

    private final KeyValueStore mSettings;

    private boolean mEnabled;
    private int mValue;
    private int mLevel;
    private float mDamage;
    private float mRange;
    private float mReloadTime;
    private float mDamageInflicted;
    private boolean mReloaded = false;

    private Plateau mPlateau;

    private TickTimer mReloadTimer;
    private RangeIndicator mRangeIndicator;
    private LevelIndicator mLevelIndicator;

    private final List<TowerListener> mListeners = new CopyOnWriteArrayList<>();

    Tower(GameEngine gameEngine, KeyValueStore settings) {
        super(gameEngine);

        mSettings = settings;

        mValue = mSettings.getInt("value");
        mDamage = mSettings.getFloat("damage");
        mRange = mSettings.getFloat("range");
        mReloadTime = mSettings.getFloat("reload");
        mLevel = 1;

        mReloadTimer = TickTimer.createInterval(mReloadTime);

        setEnabled(false);
    }

    @Override
    public final int getEntityType() {
        return Types.TOWER;
    }

    @Override
    public void clean() {
        super.clean();
        hideRange();

        if (mPlateau != null) {
            mPlateau.setOccupied(false);
            mPlateau = null;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled && !mReloaded && mReloadTimer.tick()) {
            mReloaded = true;
        }
    }

    public Aimer getAimer() {
        return null;
    }

    public abstract void preview(Canvas canvas);

    public abstract List<TowerInfoValue> getTowerInfoValues();

    public Plateau getPlateau() {
        return mPlateau;
    }

    public void setPlateau(Plateau plateau) {
        if (plateau.isOccupied()) {
            throw new RuntimeException("Plateau already occupied!");
        }

        mPlateau = plateau;
        mPlateau.setOccupied(true);
        setPosition(mPlateau.getPosition());
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;

        if (mEnabled) {
            mReloaded = true;
        }
    }

    public WeaponType getWeaponType() {
        return WeaponType.valueOf(mSettings.getString("weaponType"));
    }

    public boolean isReloaded() {
        return mReloaded;
    }

    public void setReloaded(boolean reloaded) {
        mReloaded = reloaded;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;

        for (TowerListener listener : mListeners) {
            listener.valueChanged(mValue);
        }
    }

    public float getDamage() {
        return mDamage;
    }

    public float getRange() {
        return mRange;
    }

    public float getReloadTime() {
        return mReloadTime;
    }

    public float getDamageInflicted() {
        return mDamageInflicted;
    }

    public void reportDamageInflicted(float amount) {
        mDamageInflicted += amount;

        for (TowerListener listener : mListeners) {
            listener.damageInflicted(mDamageInflicted);
        }
    }

    void setDamageInflicted(float damageInflicted) {
        mDamageInflicted = damageInflicted;
    }

    public boolean isUpgradeable() {
        return mSettings.hasKey("upgrade");
    }

    public String getUpgradeName() {
        return isUpgradeable() ? mSettings.getString("upgrade") : null;
    }

    public int getUpgradeCost() {
        return isUpgradeable() ? mSettings.getInt("upgradeCost") : 0;
    }

    public void enhance() {
        mValue += getEnhanceCost();
        mDamage += mSettings.getFloat("enhanceDamage") * (float) Math.pow(mSettings.getFloat("enhanceBase"), mLevel - 1);
        mRange += mSettings.getFloat("enhanceRange");
        mReloadTime -= mSettings.getFloat("enhanceReload");

        mLevel++;

        mReloadTimer.setInterval(mReloadTime);
    }

    public boolean isEnhanceable() {
        return mLevel < mSettings.getInt("maxLevel");
    }

    public int getEnhanceCost() {
        if (!isEnhanceable()) {
            return -1;
        }

        return Math.round(mSettings.getInt("enhanceCost") * (float) Math.pow(mSettings.getFloat("enhanceBase"), mLevel - 1));
    }

    public int getLevel() {
        return mLevel;
    }

    public int getMaxLevel() {
        return mSettings.getInt("maxLevel");
    }

    public void showRange() {
        if (mRangeIndicator == null) {
            mRangeIndicator = new RangeIndicator(getTheme(), this);
            getGameEngine().add(mRangeIndicator);
        }
    }

    public void hideRange() {
        if (mRangeIndicator != null) {
            getGameEngine().remove(mRangeIndicator);
            mRangeIndicator = null;
        }
    }

    public void showLevel() {
        if (mLevelIndicator == null) {
            mLevelIndicator = new LevelIndicator(getTheme(), this);
            getGameEngine().add(mLevelIndicator);
        }
    }

    public void hideLevel() {
        if (mLevelIndicator != null) {
            getGameEngine().remove(mLevelIndicator);
            mLevelIndicator = null;
        }
    }

    public StreamIterator<Enemy> getPossibleTargets() {
        return getGameEngine().getEntitiesByType(Types.ENEMY)
                .filter(inRange(getPosition(), getRange()))
                .cast(Enemy.class);
    }

    public void addListener(TowerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(TowerListener listener) {
        mListeners.remove(listener);
    }
}
