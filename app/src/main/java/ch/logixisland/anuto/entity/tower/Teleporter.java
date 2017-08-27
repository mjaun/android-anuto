package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.setting.tower.TeleporterSettings;
import ch.logixisland.anuto.data.setting.tower.TowerSettingsRoot;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.entity.EntityListener;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.effect.TeleportEffect;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class Teleporter extends AimingTower implements SpriteTransformation {

    private final static String ENTITY_NAME = "teleporter";

    public static class Factory implements EntityFactory {
        @Override
        public String getEntityName() {
            return ENTITY_NAME;
        }

        @Override
        public Entity create(GameEngine gameEngine) {
            TowerSettingsRoot towerSettingsRoot = gameEngine.getGameConfiguration().getTowerSettingsRoot();
            return new Teleporter(gameEngine, towerSettingsRoot.getTeleporterSettings());
        }
    }

    public static class Persister extends TowerPersister {
        public Persister(GameEngine gameEngine, EntityRegistry entityRegistry) {
            super(gameEngine, entityRegistry, ENTITY_NAME);
        }
    }

    private static class StaticData implements EntityListener {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateTower;
        Collection<Enemy> mTeleportedEnemies = new ArrayList<>();

        @Override
        public void entityRemoved(Entity entity) {
            Enemy enemy = (Enemy) entity;
            mTeleportedEnemies.remove(enemy);
        }
    }

    private TeleporterSettings mSettings;

    private float mTeleportDistance;

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteTower;
    private Sound mSound;

    private Teleporter(GameEngine gameEngine, TeleporterSettings settings) {
        super(gameEngine, settings);
        StaticData s = (StaticData) getStaticData();

        mSettings = settings;
        mTeleportDistance = settings.getTeleportDistance();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(RandomUtils.next(4));

        mSpriteTower = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateTower);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(RandomUtils.next(4));

        mSound = getSoundFactory().createSound(R.raw.gas3_hht);
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.attr.base4, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateTower = getSpriteFactory().createTemplate(R.attr.teleportTower, 4);
        s.mSpriteTemplateTower.setMatrix(0.8f, 0.8f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSpriteBase);
        getGameEngine().add(mSpriteTower);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSpriteBase);
        getGameEngine().remove(mSpriteTower);
    }

    @Override
    public void enhance() {
        super.enhance();
        mTeleportDistance += mSettings.getEnhanceTeleportDistance();
    }

    @Override
    public void tick() {
        super.tick();

        Enemy target = getTarget();

        if (isReloaded() && target != null) {
            // double check because two TeleportTowers might shoot simultaneously
            if (target.isEnabled() && getDistanceTo(target) <= getRange()) {
                StaticData s = (StaticData) getStaticData();
                s.mTeleportedEnemies.add(target);
                getGameEngine().add(new TeleportEffect(this, getPosition(), target, mTeleportDistance));
                mSound.play();
                setReloaded(false);
            } else {
                setTarget(null);
            }
        }
    }

    @Override
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }

    @Override
    public List<TowerInfoValue> getTowerInfoValues() {
        List<TowerInfoValue> properties = new ArrayList<>();
        properties.add(new TowerInfoValue(R.string.distance, mTeleportDistance));
        properties.add(new TowerInfoValue(R.string.reload, getReloadTime()));
        properties.add(new TowerInfoValue(R.string.range, getRange()));
        return properties;
    }

    @Override
    public StreamIterator<Enemy> getPossibleTargets() {
        StaticData s = (StaticData) getStaticData();

        return super.getPossibleTargets()
                .filter(s.mTeleportedEnemies)
                .filter(Enemy.enabled());
    }
}
