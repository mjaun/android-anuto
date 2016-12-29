package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.plateau.Plateau;
import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.util.data.Path;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.engine.render.shape.RangeIndicator;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.vector.Intersections;
import ch.logixisland.anuto.util.math.MathUtils;
import ch.logixisland.anuto.util.math.vector.Vector2;

public abstract class Tower extends Entity {

    class PathSection {
        Vector2 p1;
        Vector2 p2;
        float len;
    }

    private TowerConfig mConfig;

    private int mValue;
    private int mLevel;
    private float mDamage;
    private float mRange;
    private float mReloadTime;
    private float mDamageInflicted;

    private Plateau mPlateau = null;
    private boolean mReloaded = false;

    private TickTimer mReloadTimer;
    private RangeIndicator mRangeIndicator;

    private final List<TowerListener> mListeners = new CopyOnWriteArrayList<>();

    public Tower() {
        mConfig = getLevel().getTowerConfig(this);

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
        setPlateau(null);
    }

    @Override
    public void tick() {
        super.tick();

        if (isEnabled() && !mReloaded && mReloadTimer.tick()) {
            mReloaded = true;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            mReloaded = true;
        }
    }

    public abstract void preview(Canvas canvas);


    public Plateau getPlateau() {
        return mPlateau;
    }

    public void setPlateau(Plateau plateau) {
        if (mPlateau != null) {
            mPlateau.setOccupant(null);
        }

        mPlateau = plateau;

        if (mPlateau != null) {
            mPlateau.setOccupant(this);
            setPosition(mPlateau.getPosition());
        }
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


    public Tower upgrade() {
        Plateau plateau = this.getPlateau();
        Tower upgrade;

        try {
            upgrade = mConfig.getUpgradeTowerConfig().getTowerClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        int cost = getUpgradeCost();
        upgrade.mValue = this.mValue + cost;

        this.remove();
        upgrade.setPlateau(plateau);
        upgrade.setEnabled(true);
        getGameEngine().add(upgrade);

        return upgrade;
    }

    public boolean isUpgradeable() {
        return mConfig.getUpgradeTowerConfig() != null;
    }

    public int getUpgradeCost() {
        if (!isUpgradeable()) {
            return -1;
        }

        return mConfig.getUpgradeTowerConfig().getValue() - mConfig.getValue();
    }

    public void enhance() {
        mValue += getEnhanceCost();
        mDamage += mConfig.getEnhanceDamage() * (float)Math.pow(mConfig.getEnhanceBase(), mLevel - 1);
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

        return Math.round(mConfig.getEnhanceCost() * (float)Math.pow(mConfig.getEnhanceBase(), mLevel - 1));
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


    public StreamIterator<Enemy> getPossibleTargets() {
        return getGameEngine().get(Types.ENEMY)
                .filter(inRange(getPosition(), getRange()))
                .cast(Enemy.class);
    }

    public List<PathSection> getPathSections() {
        List<PathSection> ret = new ArrayList<>();

        float r2 = MathUtils.square(getRange());

        for (Path p : getLevel().getPaths()) {
            for (int i = 1; i < p.size(); i++) {
                Vector2 p1 = p.get(i - 1).copy().sub(getPosition());
                Vector2 p2 = p.get(i).copy().sub(getPosition());

                boolean p1in = p1.len2() <= r2;
                boolean p2in = p2.len2() <= r2;

                Vector2[] is = Intersections.lineCircle(p1, p2, getRange());

                PathSection s = new PathSection();

                if (p1in && p2in) {
                    s.p1 = p1.add(getPosition());
                    s.p2 = p2.add(getPosition());
                } else if (!p1in && !p2in) {
                    if (is == null) {
                        continue;
                    }

                    float a1 = Vector2.fromTo(is[0], p1).angle();
                    float a2 = Vector2.fromTo(is[0], p2).angle();

                    if (MathUtils.equals(a1, a2, 10f)) {
                        continue;
                    }

                    s.p1 = is[0].add(getPosition());
                    s.p2 = is[1].add(getPosition());
                }
                else {
                    float angle = Vector2.fromTo(p1, p2).angle();

                    if (p1in) {
                        if (MathUtils.equals(angle, Vector2.fromTo(p1, is[0]).angle(), 10f)) {
                            s.p2 = is[0].add(getPosition());
                        } else {
                            s.p2 = is[1].add(getPosition());
                        }

                        s.p1 = p1.add(getPosition());
                    } else {
                        if (MathUtils.equals(angle, Vector2.fromTo(is[0], p2).angle(), 10f)) {
                            s.p1 = is[0].add(getPosition());
                        } else {
                            s.p1 = is[1].add(getPosition());
                        }

                        s.p2 = p2.add(getPosition());
                    }
                }

                s.len = Vector2.fromTo(s.p1, s.p2).len();
                ret.add(s);
            }
        }

        return ret;
    }


    public TowerConfig getConfig() {
        return mConfig;
    }

    public float getProperty(String name) {
        return mConfig.getProperties().get(name);
    }


    public void addListener(TowerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(TowerListener listener) {
        mListeners.remove(listener);
    }
}
