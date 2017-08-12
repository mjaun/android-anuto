package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.data.setting.tower.MortarSettings;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformation;
import ch.logixisland.anuto.engine.render.sprite.SpriteTransformer;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.shot.MortarShot;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.Vector2;

public class Mortar extends AimingTower implements SpriteTransformation {

    private final static float SHOT_SPAWN_OFFSET = 0.6f;
    private final static float REBOUND_DURATION = 0.5f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private MortarSettings mSettings;

    private float mExplosionRadius;
    private float mAngle = 90f;
    private boolean mRebounding = false;

    private StaticSprite mSpriteBase;
    private AnimatedSprite mSpriteCanon;
    private Sound mSound;

    public Mortar(GameEngine gameEngine, MortarSettings settings) {
        super(gameEngine, settings);
        StaticData s = (StaticData) getStaticData();

        mSettings = settings;
        mExplosionRadius = mSettings.getExplosionRadius();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setIndex(RandomUtils.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createAnimated(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setSequenceForwardBackward();
        mSpriteCanon.setInterval(REBOUND_DURATION);

        mSound = getSoundFactory().createSound(R.raw.gas2_thomp);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.attr.base2, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.attr.mortar, 8);
        s.mSpriteTemplateCanon.setMatrix(0.8f, null, new Vector2(0.4f, 0.2f), -90f);

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
    public void enhance() {
        super.enhance();
        mExplosionRadius += mSettings.getEnhanceExplosionRadius();
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null && isReloaded()) {
            Vector2 targetPos = getTarget().getPositionAfter(MortarShot.TIME_TO_TARGET);
            targetPos = targetPos.add(Vector2.polar(RandomUtils.next(mSettings.getInaccuracy()), RandomUtils.next(360f)));
            mAngle = getAngleTo(targetPos);
            Vector2 shotPos = getPosition().add(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));

            getGameEngine().add(new MortarShot(this, shotPos, targetPos, getDamage(), mExplosionRadius));
            mSound.play();

            setReloaded(false);
            mRebounding = true;
        }

        if (mRebounding && mSpriteCanon.tick()) {
            mRebounding = false;
        }
    }

    @Override
    public void draw(SpriteInstance sprite, SpriteTransformer transformer) {
        transformer.translate(getPosition());

        if (sprite == mSpriteCanon) {
            transformer.rotate(mAngle);
        }
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
        properties.add(new TowerInfoValue(R.string.splash, mExplosionRadius));
        properties.add(new TowerInfoValue(R.string.reload, getReloadTime()));
        properties.add(new TowerInfoValue(R.string.range, getRange()));
        properties.add(new TowerInfoValue(R.string.inflicted, getDamageInflicted()));
        return properties;
    }
}
