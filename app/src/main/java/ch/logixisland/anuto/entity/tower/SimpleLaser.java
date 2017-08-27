package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.setting.tower.TowerSettings;
import ch.logixisland.anuto.data.setting.tower.TowerSettingsRoot;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.effect.BouncingLaser;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.Vector2;

public class SimpleLaser extends AimingTower implements SpriteTransformation {

    private final static String ENTITY_NAME = "simpleLaser";
    private final static float LASER_SPAWN_OFFSET = 0.7f;

    public static class Factory implements EntityFactory {
        @Override
        public String getEntityName() {
            return ENTITY_NAME;
        }

        @Override
        public Entity create(GameEngine gameEngine) {
            TowerSettingsRoot towerSettingsRoot = gameEngine.getGameConfiguration().getTowerSettingsRoot();
            return new SimpleLaser(gameEngine, towerSettingsRoot.getSimpleLaserSettings());
        }
    }

    public static class Persister extends TowerPersister {
        public Persister(GameEngine gameEngine, EntityRegistry entityRegistry) {
            super(gameEngine, entityRegistry, ENTITY_NAME);
        }
    }

    private static class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private float mAngle = 90f;

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteCanon;
    private Sound mSound;

    private SimpleLaser(GameEngine gameEngine, TowerSettings settings) {
        super(gameEngine, settings);
        StaticData s = (StaticData) getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setIndex(RandomUtils.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setIndex(RandomUtils.next(4));
        mSpriteCanon.setListener(this);

        mSound = getSoundFactory().createSound(R.raw.laser1_zz);
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.attr.base5, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, -90f);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.attr.laserTower1, 4);
        s.mSpriteTemplateCanon.setMatrix(0.4f, 0.9f, new Vector2(0.2f, 0.2f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSpriteBase);
        getGameEngine().add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSpriteBase);
        getGameEngine().remove(mSpriteCanon);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Vector2 from = getPosition().add(Vector2.polar(LASER_SPAWN_OFFSET, mAngle));
                getGameEngine().add(new BouncingLaser(this, from, getTarget(), getDamage()));
                setReloaded(false);
                mSound.play();
            }
        }
    }

    @Override
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());
        transformer.rotate(mAngle);
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }

    @Override
    public List<TowerInfoValue> getTowerInfoValues() {
        List<TowerInfoValue> properties = new ArrayList<>();
        properties.add(new TowerInfoValue(R.string.damage, getDamage()));
        properties.add(new TowerInfoValue(R.string.reload, getReloadTime()));
        properties.add(new TowerInfoValue(R.string.range, getRange()));
        properties.add(new TowerInfoValue(R.string.inflicted, getDamageInflicted()));
        return properties;
    }
}
