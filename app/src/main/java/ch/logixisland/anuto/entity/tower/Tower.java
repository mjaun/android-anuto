package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.engine.render.shape.LevelIndicator;
import ch.logixisland.anuto.engine.render.shape.RangeIndicator;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.data.PathDescriptor;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.data.WeaponType;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.vector.Intersections;
import ch.logixisland.anuto.util.math.vector.Line;
import ch.logixisland.anuto.util.math.vector.Vector2;

public abstract class Tower extends Entity {

    private final TowerConfig mConfig;

    private boolean mEnabled;
    private int mValue;
    private int mLevel;
    private float mDamage;
    private float mRange;
    private float mReloadTime;
    private float mDamageInflicted;
    private boolean mReloaded = false;

    private TickTimer mReloadTimer;
    private RangeIndicator mRangeIndicator;
    private LevelIndicator mLevelIndicator;

    private final List<TowerListener> mListeners = new CopyOnWriteArrayList<>();

    public Tower(TowerConfig config) {
        mConfig = config;

        mValue = mConfig.getValue();
        mDamage = mConfig.getDamage();
        mRange = mConfig.getRange();
        mReloadTime = mConfig.getReload();
        mLevel = 1;

        mReloadTimer = TickTimer.createInterval(mReloadTime);

        setEnabled(false);
    }


    @Override
    public final int getType() {
        return Types.TOWER;
    }

    @Override
    public void clean() {
        super.clean();
        hideRange();
    }

    @Override
    public void tick() {
        super.tick();

        if (mEnabled && !mReloaded && mReloadTimer.tick()) {
            mReloaded = true;
        }
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;

        if (mEnabled) {
            mReloaded = true;
        }
    }


    public abstract void preview(Canvas canvas);

    public abstract List<TowerProperty> getProperties();


    public String getName() {
        return mConfig.getName();
    }

    public WeaponType getWeaponType() {
        return mConfig.getWeaponType();
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

    public void reportDamageInflicted(float damage) {
        mDamageInflicted += damage;

        for (TowerListener listener : mListeners) {
            listener.damageInflicted(mDamageInflicted);
        }
    }

    public boolean isUpgradeable() {
        return mConfig.getUpgrade() != null;
    }

    public String getUpgradeName() {
        return mConfig.getUpgrade();
    }

    public int getUpgradeCost() {
        return mConfig.getUpgradeCost();
    }

    public void enhance() {
        mValue += getEnhanceCost();
        mDamage += mConfig.getEnhanceDamage() * (float) Math.pow(mConfig.getEnhanceBase(), mLevel - 1);
        mRange += mConfig.getEnhanceRange();
        mReloadTime -= mConfig.getEnhanceReload();

        mLevel++;

        mReloadTimer.setInterval(mReloadTime);
    }

    public boolean isEnhanceable() {
        return mLevel < mConfig.getMaxLevel();
    }

    public int getEnhanceCost() {
        if (!isEnhanceable()) {
            return -1;
        }

        return Math.round(mConfig.getEnhanceCost() * (float) Math.pow(mConfig.getEnhanceBase(), mLevel - 1));
    }

    public int getTowerLevel() {
        return mLevel;
    }

    public int getTowerLevelMax() {
        return mConfig.getMaxLevel();
    }


    public void showRange() {
        if (mRangeIndicator == null) {
            mRangeIndicator = getShapeFactory().createRangeIndicator(this);
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
            mLevelIndicator = getShapeFactory().createLevelIndicator(this);
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
        return getGameEngine().get(Types.ENEMY)
                .filter(inRange(getPosition(), getRange()))
                .cast(Enemy.class);
    }

    List<Line> getPathSectionsInRange() {
        List<Line> sections = new ArrayList<>();

        float r2 = MathUtils.square(getRange());

        for (PathDescriptor path : getLevelDescriptor().getPaths()) {
            List<Vector2> wayPoints = path.getWayPoints();
            for (int i = 1; i < wayPoints.size(); i++) {
                Vector2 p1 = wayPoints.get(i - 1).copy().sub(getPosition());
                Vector2 p2 = wayPoints.get(i).copy().sub(getPosition());

                boolean p1in = p1.len2() <= r2;
                boolean p2in = p2.len2() <= r2;

                Vector2[] is = Intersections.lineCircle(p1, p2, getRange());

                Line section = new Line();

                if (p1in && p2in) {
                    section.setPoint1(p1.add(getPosition()));
                    section.setPoint2(p2.add(getPosition()));
                } else if (!p1in && !p2in) {
                    if (is == null) {
                        continue;
                    }

                    float a1 = Vector2.fromTo(is[0], p1).angle();
                    float a2 = Vector2.fromTo(is[0], p2).angle();

                    if (MathUtils.equals(a1, a2, 10f)) {
                        continue;
                    }

                    section.setPoint1(is[0].add(getPosition()));
                    section.setPoint2(is[1].add(getPosition()));
                } else {
                    float angle = Vector2.fromTo(p1, p2).angle();

                    if (p1in) {
                        if (MathUtils.equals(angle, Vector2.fromTo(p1, is[0]).angle(), 10f)) {
                            section.setPoint2(is[0].add(getPosition()));
                        } else {
                            section.setPoint2(is[1].add(getPosition()));
                        }

                        section.setPoint1(p1.add(getPosition()));
                    } else {
                        if (MathUtils.equals(angle, Vector2.fromTo(is[0], p2).angle(), 10f)) {
                            section.setPoint1(is[0].add(getPosition()));
                        } else {
                            section.setPoint1(is[1].add(getPosition()));
                        }

                        section.setPoint2(p2.add(getPosition()));
                    }
                }

                sections.add(section);
            }
        }

        return sections;
    }


    float getProperty(String name) {
        return mConfig.getProperties().get(name);
    }


    public void addListener(TowerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(TowerListener listener) {
        mListeners.remove(listener);
    }

}
