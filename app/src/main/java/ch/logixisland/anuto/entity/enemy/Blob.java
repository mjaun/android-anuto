package ch.logixisland.anuto.entity.enemy;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.setting.enemy.EnemySettings;
import ch.logixisland.anuto.data.setting.enemy.EnemySettingsRoot;
import ch.logixisland.anuto.data.setting.enemy.GlobalSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.entity.Entity;
import ch.logixisland.anuto.engine.logic.entity.EntityFactory;
import ch.logixisland.anuto.engine.logic.loop.TickListener;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.ReplicatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;

public class Blob extends Enemy implements SpriteTransformation {

    private final static float ANIMATION_SPEED = 1.5f;

    public static class Factory implements EntityFactory {
        @Override
        public Entity create(GameEngine gameEngine) {
            EnemySettingsRoot enemySettingsRoot = gameEngine.getGameConfiguration().getEnemySettingsRoot();
            return new Blob(gameEngine, enemySettingsRoot.getGlobalSettings(), enemySettingsRoot.getBlobSettings());
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

    private ReplicatedSprite mSprite;

    private Blob(GameEngine gameEngine, GlobalSettings globalSettings, EnemySettings enemySettings) {
        super(gameEngine, globalSettings, enemySettings);
        StaticData s = (StaticData) getStaticData();

        mSprite = getSpriteFactory().createReplication(s.mReferenceSprite);
        mSprite.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.blob, 9);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, null);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForward();
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
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());
    }
}
