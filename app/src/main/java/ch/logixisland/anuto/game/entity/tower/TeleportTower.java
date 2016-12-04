package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.effect.TeleportEffect;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class TeleportTower extends AimingTower {

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateTower;
    }

    private final Listener mEnemyListener = new Listener() {
        @Override
        public void onObjectAdded(Entity obj) {

        }

        @Override
        public void onObjectRemoved(Entity obj) {
            mTeleportedEnemies.remove(obj);
            obj.removeListener(this);
        }
    };

    private List<Enemy> mTeleportedEnemies = new ArrayList<>();

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteTower;

    public TeleportTower() {
        StaticData s = (StaticData)getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(RandomUtils.next(4));

        mSpriteTower = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateTower);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(RandomUtils.next(4));
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.drawable.base4, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateTower = getSpriteFactory().createTemplate(R.drawable.teleport_tower, 4);
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
    public void tick() {
        super.tick();

        Enemy target = getTarget();

        if (isReloaded() && target != null) {
            // double check because two TeleportTowers might shoot simultaneously
            if (!target.isEnabled() || getDistanceTo(target) > getRange()) {
                setTarget(null);
            } else {
                getGameEngine().add(new TeleportEffect(this, getPosition(), target, getDamage()));
                setReloaded(false);

                mTeleportedEnemies.add(target);
                target.addListener(mEnemyListener);
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }

    @Override
    public StreamIterator<Enemy> getPossibleTargets() {
        return super.getPossibleTargets()
                .filter(Entity.enabled())
                .exclude(mTeleportedEnemies);
    }
}
