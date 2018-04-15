package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.setting.tower.RocketLauncherSettings;
import ch.logixisland.anuto.data.setting.tower.TowerSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.TickTimer;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.shot.Rocket;
import ch.logixisland.anuto.util.RandomUtils;

public class RocketLauncher extends AimingTower implements SpriteTransformation {

    private final static String ENTITY_NAME = "rocketLauncher";
    private final static float ROCKET_LOAD_TIME = 1.0f;

    public static class Factory implements EntityFactory {
        @Override
        public String getEntityName() {
            return ENTITY_NAME;
        }

        @Override
        public Entity create(GameEngine gameEngine) {
            TowerSettings towerSettings = gameEngine.getGameConfiguration().getGameSettings().getTowerSettings();
            return new RocketLauncher(gameEngine, towerSettings.getRocketLauncherSettings());
        }
    }

    public static class Persister extends AimingTowerPersister {
        public Persister(GameEngine gameEngine, EntityRegistry entityRegistry) {
            super(gameEngine, entityRegistry, ENTITY_NAME);
        }
    }

    private static class StaticData {
        SpriteTemplate mSpriteTemplate;
        SpriteTemplate mSpriteTemplateRocket; // used for preview only
    }

    private RocketLauncherSettings mSettings;

    private float mExplosionRadius;
    private float mAngle = 90f;
    private Rocket mRocket;
    private TickTimer mRocketLoadTimer;

    private StaticSprite mSprite;
    private StaticSprite mSpriteRocket; // used for preview only
    private Sound mSound;

    private RocketLauncher(GameEngine gameEngine, RocketLauncherSettings settings) {
        super(gameEngine, settings);
        StaticData s = (StaticData) getStaticData();

        mSettings = settings;

        mSprite = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setIndex(RandomUtils.next(4));

        mSpriteRocket = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateRocket);
        mSpriteRocket.setListener(this);
        mSpriteRocket.setIndex(RandomUtils.next(4));

        mExplosionRadius = settings.getExplosionRadius();
        mRocketLoadTimer = TickTimer.createInterval(ROCKET_LOAD_TIME);

        mSound = getSoundFactory().createSound(R.raw.explosive2_tsh);
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.rocketLauncher, 4);
        s.mSpriteTemplate.setMatrix(1.1f, 1.1f, null, -90f);

        s.mSpriteTemplateRocket = getSpriteFactory().createTemplate(R.attr.rocket, 4);
        s.mSpriteTemplateRocket.setMatrix(0.8f, 1f, null, -90f);

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

        if (mRocket != null) {
            mRocket.remove();
        }
    }

    @Override
    public void enhance() {
        super.enhance();
        mExplosionRadius += mSettings.getEnhanceExplosionRadius();
    }

    @Override
    public void tick() {
        super.tick();

        if (mRocket == null && mRocketLoadTimer.tick()) {
            mRocket = new Rocket(this, getPosition(), getDamage(), mExplosionRadius);
            mRocket.setAngle(mAngle);
            getGameEngine().add(mRocket);
        }

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (mRocket != null) {
                mRocket.setAngle(mAngle);

                if (isReloaded()) {
                    mRocket.setTarget(getTarget());
                    mRocket.setEnabled(true);
                    mRocket = null;
                    mSound.play();

                    setReloaded(false);
                }
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
        mSprite.draw(canvas);
        mSpriteRocket.draw(canvas);
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
}
