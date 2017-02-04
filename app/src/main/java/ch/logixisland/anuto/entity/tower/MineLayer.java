package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.EntityListener;
import ch.logixisland.anuto.entity.shot.Mine;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.math.vector.Line;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class MineLayer extends Tower {

    private final static float ANIMATION_DURATION = 1f;

    private class StaticData {
        public SpriteTemplate mSpriteTemplate;
    }

    private float mAngle;
    private int mMaxMineCount;
    private float mExplosionRadius;
    private boolean mShooting;
    private List<Line> mSections;
    private List<Mine> mMines = new ArrayList<>();

    private AnimatedSprite mSprite;
    private Sound mSound;

    private final EntityListener mMineListener = new EntityListener() {

        @Override
        public void entityRemoved(Entity obj) {
            mMines.remove(obj);
            obj.removeListener(this);
        }
    };

    public MineLayer(TowerConfig config) {
        super(config);
        StaticData s = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createAnimated(Layers.TOWER_BASE, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setSequenceForwardBackward();
        mSprite.setInterval(ANIMATION_DURATION);

        mAngle = RandomUtils.next(360f);
        mMaxMineCount = (int) getProperty("maxMineCount");
        mExplosionRadius = getProperty("explosionRadius");

        mSound = getSoundFactory().createSound(R.raw.gun2_donk);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.mineLayer, 6);
        s.mSpriteTemplate.setMatrix(1f, 1f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);

        for (Mine m : mMines) {
            m.removeListener(mMineListener);
            m.remove();
        }

        mMines.clear();
    }

    @Override
    public void setPosition(Vector2 position) {
        super.setPosition(position);
        mSections = getPathSectionsInRange();
    }

    @Override
    public void move(Vector2 offset) {
        super.move(offset);
        mSections = getPathSectionsInRange();
    }

    @Override
    public void enhance() {
        super.enhance();
        mMaxMineCount += getProperty("enhanceMaxMineCount");
        mExplosionRadius += getProperty("enhanceExplosionRadius");
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && mMines.size() < mMaxMineCount && mSections.size() > 0) {
            mShooting = true;
            setReloaded(false);
        }

        if (mShooting) {
            mSprite.tick();

            if (mSprite.getSequenceIndex() == 5) {
                Mine m = new Mine(this, getPosition(), getTarget(), getDamage(), mExplosionRadius);
                m.addListener(mMineListener);
                mMines.add(m);
                getGameEngine().add(m);
                mSound.play();

                mShooting = false;
            }
        }

        if (mSprite.getSequenceIndex() != 0) {
            mSprite.tick();
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSprite.draw(canvas);
    }

    @Override
    public List<TowerProperty> getProperties() {
        List<TowerProperty> properties = new ArrayList<>();
        properties.add(new TowerProperty(R.string.damage, getDamage()));
        properties.add(new TowerProperty(R.string.splash, mExplosionRadius));
        properties.add(new TowerProperty(R.string.reload, getReloadTime()));
        properties.add(new TowerProperty(R.string.range, getRange()));
        properties.add(new TowerProperty(R.string.inflicted, getDamageInflicted()));
        return properties;
    }

    private Vector2 getTarget() {
        float totalLen = 0f;

        for (Line section : mSections) {
            totalLen += section.length();
        }

        float dist = RandomUtils.next(totalLen);

        for (Line section : mSections) {
            float length = section.length();

            if (dist > length) {
                dist -= length;
            } else {
                return section.lineVector()
                        .norm()
                        .mul(dist)
                        .add(section.getPoint1());
            }
        }

        return null;
    }
}
