package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.data.map.PathDescriptor;
import ch.logixisland.anuto.data.setting.enemy.WeaponType;
import ch.logixisland.anuto.data.setting.tower.BasicTowerSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.Intersections;
import ch.logixisland.anuto.util.math.Line;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.Vector2;

public abstract class Tower extends Entity {

    private final BasicTowerSettings mSettings;

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

    Tower(GameEngine gameEngine, BasicTowerSettings settings) {
        super(gameEngine);

        mSettings = settings;

        mValue = mSettings.getValue();
        mDamage = mSettings.getDamage();
        mRange = mSettings.getRange();
        mReloadTime = mSettings.getReload();
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
        return mSettings.getWeaponType();
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
        return mSettings.getUpgrade() != null;
    }

    public String getUpgradeName() {
        return mSettings.getUpgrade();
    }

    public int getUpgradeCost() {
        return mSettings.getUpgradeCost();
    }

    public void enhance() {
        mValue += getEnhanceCost();
        mDamage += mSettings.getEnhanceDamage() * (float) Math.pow(mSettings.getEnhanceBase(), mLevel - 1);
        mRange += mSettings.getEnhanceRange();
        mReloadTime -= mSettings.getEnhanceReload();

        mLevel++;

        mReloadTimer.setInterval(mReloadTime);
    }

    public boolean isEnhanceable() {
        return mLevel < mSettings.getMaxLevel();
    }

    public int getEnhanceCost() {
        if (!isEnhanceable()) {
            return -1;
        }

        return Math.round(mSettings.getEnhanceCost() * (float) Math.pow(mSettings.getEnhanceBase(), mLevel - 1));
    }

    public int getLevel() {
        return mLevel;
    }

    public int getMaxLevel() {
        return mSettings.getMaxLevel();
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

    Collection<Line> getPathSectionsInRange(Collection<PathDescriptor> paths) {
        Collection<Line> sections = new ArrayList<>();

        for (PathDescriptor path : paths) {
            sections.addAll(getPathSectionsInRange(path));
        }

        return sections;
    }

    private Collection<Line> getPathSectionsInRange(PathDescriptor path) {
        float r2 = MathUtils.square(getRange());
        Collection<Line> sections = new ArrayList<>();
        List<Vector2> wayPoints = path.getWayPoints();

        for (int i = 1; i < wayPoints.size(); i++) {
            Vector2 p1 = getPosition().to(wayPoints.get(i - 1));
            Vector2 p2 = getPosition().to(wayPoints.get(i));

            boolean p1in = p1.len2() <= r2;
            boolean p2in = p2.len2() <= r2;

            Vector2[] is = Intersections.lineCircle(p1, p2, getRange());

            Vector2 sectionP1;
            Vector2 sectionP2;

            if (p1in && p2in) {
                sectionP1 = p1.add(getPosition());
                sectionP2 = p2.add(getPosition());
            } else if (!p1in && !p2in) {
                if (is == null) {
                    continue;
                }

                float a1 = is[0].to(p1).angle();
                float a2 = is[0].to(p2).angle();

                if (MathUtils.equals(a1, a2, 10f)) {
                    continue;
                }

                sectionP1 = is[0].add(getPosition());
                sectionP2 = is[1].add(getPosition());
            } else {
                float angle = p1.to(p2).angle();

                if (p1in) {
                    if (MathUtils.equals(angle, p1.to(is[0]).angle(), 10f)) {
                        sectionP2 = is[0].add(getPosition());
                    } else {
                        sectionP2 = is[1].add(getPosition());
                    }

                    sectionP1 = (p1.add(getPosition()));
                } else {
                    if (MathUtils.equals(angle, is[0].to(p2).angle(), 10f)) {
                        sectionP1 = is[0].add(getPosition());
                    } else {
                        sectionP1 = is[1].add(getPosition());
                    }

                    sectionP2 = p2.add(getPosition());
                }
            }

            sections.add(new Line(sectionP1, sectionP2));
        }

        return sections;
    }

    public void addListener(TowerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(TowerListener listener) {
        mListeners.remove(listener);
    }

}
