package ch.logixisland.anuto.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.engine.sound.Sound;
import ch.logixisland.anuto.entity.effect.TeleportEffect;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class TeleportTower extends AimingTower {

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateTower;
    }

    private float mTeleportDistance;

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteTower;
    private Sound mSound;

    public TeleportTower(TowerConfig config) {
        super(config);
        mTeleportDistance = getProperty("teleportDistance");

        StaticData s = (StaticData) getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(RandomUtils.next(4));

        mSpriteTower = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateTower);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(RandomUtils.next(4));

        mSound = getSoundFactory().createSound(R.raw.gas3_hht);
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
        mTeleportDistance += getProperty("enhanceTeleportDistance");
    }

    @Override
    public void tick() {
        super.tick();

        Enemy target = getTarget();

        if (isReloaded() && target != null) {
            // double check because two TeleportTowers might shoot simultaneously
            if (!target.isEnabled() || getDistanceTo(target) > getRange()) {
                setTarget(null);
            } else {
                getGameEngine().add(new TeleportEffect(this, getPosition(), target, mTeleportDistance));
                mSound.play();
                setReloaded(false);
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }

    @Override
    public List<TowerProperty> getProperties() {
        List<TowerProperty> properties = new ArrayList<>();
        properties.add(new TowerProperty(R.string.distance, mTeleportDistance));
        properties.add(new TowerProperty(R.string.reload, getReloadTime()));
        properties.add(new TowerProperty(R.string.range, getRange()));
        return properties;
    }

    @Override
    public StreamIterator<Enemy> getPossibleTargets() {
        return super.getPossibleTargets().filter(Enemy.enabled());
    }
}
