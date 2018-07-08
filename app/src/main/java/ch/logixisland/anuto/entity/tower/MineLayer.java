package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.KeyValueStore;
import ch.logixisland.anuto.data.map.GameMap;
import ch.logixisland.anuto.data.map.MapPath;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.shot.Mine;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.Intersections;
import ch.logixisland.anuto.util.math.Line;
import ch.logixisland.anuto.util.math.Vector2;

public class MineLayer extends Tower implements SpriteTransformation {

    private final static String ENTITY_NAME = "mineLayer";
    private final static float ANIMATION_DURATION = 1f;

    public static class Factory implements EntityFactory {
        @Override
        public String getEntityName() {
            return ENTITY_NAME;
        }

        @Override
        public Entity create(GameEngine gameEngine) {
            KeyValueStore towerSettings = gameEngine.getGameConfiguration().getGameSettings().getStore("towerSettings");
            GameMap map = gameEngine.getGameConfiguration().getGameMap();
            return new MineLayer(gameEngine, towerSettings.getStore("mineLayer"), map.getPaths());
        }
    }

    public static class Persister extends TowerPersister {

        public Persister(GameEngine gameEngine, EntityRegistry entityRegistry) {
            super(gameEngine, entityRegistry, ENTITY_NAME);
        }

        @Override
        protected KeyValueStore writeEntityData(Entity entity) {
            MineLayer mineLayer = (MineLayer) entity;
            KeyValueStore data = super.writeEntityData(entity);

            List<Vector2> minePositions = new ArrayList<>();
            for (Mine mine : mineLayer.mMines) {
                if (!mine.isFlying()) {
                    minePositions.add(mine.getPosition());
                }
            }
            data.putVectorList("minePositions", minePositions);

            return data;
        }

        @Override
        protected MineLayer readEntityData(KeyValueStore entityData) {
            MineLayer mineLayer = (MineLayer) super.readEntityData(entityData);

            for (Vector2 minePosition : entityData.getVectorList("minePositions")) {
                Mine mine = new Mine(mineLayer, minePosition, mineLayer.getDamage(), mineLayer.mExplosionRadius);
                mineLayer.mMines.add(mine);
                mine.addListener(mineLayer.mMineListener);
                getGameEngine().add(mine);
            }

            return mineLayer;
        }
    }

    private static class StaticData {
        public SpriteTemplate mSpriteTemplate;
    }

    private KeyValueStore mSettings;
    private Collection<MapPath> mPaths;

    private float mAngle;
    private int mMaxMineCount;
    private float mExplosionRadius;
    private boolean mShooting;
    private Collection<Line> mSections;
    private Collection<Mine> mMines = new ArrayList<>();

    private AnimatedSprite mSprite;
    private Sound mSound;

    private final Listener mMineListener = new Listener() {
        @Override
        public void entityRemoved(Entity entity) {
            Mine mine = (Mine) entity;
            mine.removeListener(this);
            mMines.remove(mine);
        }
    };

    private MineLayer(GameEngine gameEngine, KeyValueStore settings, Collection<MapPath> paths) {
        super(gameEngine, settings);
        StaticData s = (StaticData) getStaticData();

        mPaths = paths;
        mSettings = settings;

        mSprite = getSpriteFactory().createAnimated(Layers.TOWER_BASE, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setSequenceForwardBackward();
        mSprite.setInterval(ANIMATION_DURATION);

        mAngle = RandomUtils.next(360f);
        mMaxMineCount = mSettings.getInt("maxMineCount");
        mExplosionRadius = mSettings.getFloat("explosionRadius");

        mSound = getSoundFactory().createSound(R.raw.gun2_donk);
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
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
        mSections = getPathSectionsInRange(mPaths);
    }

    @Override
    public void move(Vector2 offset) {
        super.move(offset);
        mSections = getPathSectionsInRange(mPaths);
    }

    @Override
    public void enhance() {
        super.enhance();
        mMaxMineCount += mSettings.getInt("enhanceMaxMineCount");
        mExplosionRadius += mSettings.getFloat("enhanceExplosionRadius");
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
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());
        transformer.rotate(mAngle);
    }

    @Override
    public void preview(Canvas canvas) {
        mSprite.draw(canvas);
    }

    @Override
    public List<TowerInfoValue> getTowerInfoValues() {
        List<TowerInfoValue> properties = new ArrayList<>();
        properties.add(new TowerInfoValue(R.string.damage, getDamage()));
        properties.add(new TowerInfoValue(R.string.splash, mExplosionRadius));
        properties.add(new TowerInfoValue(R.string.reload, getReloadTime()));
        properties.add(new TowerInfoValue(R.string.range, getRange()));
        properties.add(new TowerInfoValue(R.string.inflicted, getDamageInflicted()));
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

    private Collection<Line> getPathSectionsInRange(Collection<MapPath> paths) {
        Collection<Line> sections = new ArrayList<>();

        for (MapPath path : paths) {
            sections.addAll(Intersections.getPathSectionsInRange(path.getWayPoints(), getPosition(), getRange()));
        }

        return sections;
    }
}
