package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.shot.MortarShot;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class Mortar extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.6f;
    private final static float REBOUND_DURATION = 0.5f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private float mInaccuracy;
    private float mExplosionRadius;
    private float mAngle = 90f;
    private boolean mRebounding = false;

    private StaticSprite mSpriteBase;
    private AnimatedSprite mSpriteCanon;
    private Sound mSound;

    public Mortar(TowerConfig config) {
        super(config);
        StaticData s = (StaticData) getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setIndex(RandomUtils.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createAnimated(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setSequenceForwardBackward();
        mSpriteCanon.setInterval(REBOUND_DURATION);

        mInaccuracy = getProperty("inaccuracy");
        mExplosionRadius = getProperty("explosionRadius");

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
        mExplosionRadius += getProperty("enhanceExplosionRadius");
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        if (sprite == mSpriteCanon) {
            canvas.rotate(mAngle);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null && isReloaded()) {
            Vector2 targetPos = getTarget().getPositionAfter(MortarShot.TIME_TO_TARGET);
            targetPos.add(Vector2.polar(RandomUtils.next(mInaccuracy), RandomUtils.next(360f)));
            mAngle = getAngleTo(targetPos);
            Vector2 shotPos = getPosition().copy().add(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));

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
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
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
}
