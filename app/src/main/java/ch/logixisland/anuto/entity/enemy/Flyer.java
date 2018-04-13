package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.setting.enemy.BasicEnemySettings;
import ch.logixisland.anuto.data.setting.enemy.EnemySettings;
import ch.logixisland.anuto.data.setting.enemy.GlobalSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.entity.EntityRegistry;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.ReplicatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;

public class Flyer extends Enemy implements SpriteTransformation {

    private final static String ENTITY_NAME = "flyer";
    private final static float ANIMATION_SPEED = 1.0f;

    public static class Factory implements EntityFactory {
        @Override
        public String getEntityName() {
            return ENTITY_NAME;
        }

        @Override
        public Entity create(GameEngine gameEngine) {
            EnemySettings enemySettings = gameEngine.getGameConfiguration().getEnemySettingsRoot();
            return new Flyer(gameEngine, enemySettings.getGlobalSettings(), enemySettings.getFlyerSettings());
        }
    }

    public static class Persister extends EnemyPersister {
        public Persister(GameEngine gameEngine, EntityRegistry entityRegistry) {
            super(gameEngine, entityRegistry, ENTITY_NAME);
        }
    }

    private static class StaticData implements TickListener {
        SpriteTemplate mSpriteTemplate;
        AnimatedSprite mReferenceSprite;

        @Override
        public void tick() {
            mReferenceSprite.tick();
        }
    }

    private float mAngle;

    private ReplicatedSprite mSprite;

    private Flyer(GameEngine gameEngine, GlobalSettings globalSettings, BasicEnemySettings enemySettings) {
        super(gameEngine, globalSettings, enemySettings);
        StaticData s = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createReplication(s.mReferenceSprite);
        mSprite.setListener(this);
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.flyer, 6);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, -90f);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForwardBackward();
        s.mReferenceSprite.setFrequency(ANIMATION_SPEED);

        getGameEngine().add(s);

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
    }

    @Override
    public void tick() {
        super.tick();

        if (hasWayPoint()) {
            mAngle = getDirection().angle();
        }
    }

    @Override
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());
        transformer.rotate(mAngle);
    }
}
