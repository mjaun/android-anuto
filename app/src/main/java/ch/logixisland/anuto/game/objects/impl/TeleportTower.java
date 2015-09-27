package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.iterator.StreamIterator;

public class TeleportTower extends AimingTower {

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteTower;
    }

    private final Listener mEnemyListener = new Listener() {
        @Override
        public void onObjectAdded(GameObject obj) {

        }

        @Override
        public void onObjectRemoved(GameObject obj) {
            mTeleportedEnemies.remove(obj);
            obj.removeListener(this);
        }
    };

    private List<Enemy> mTeleportedEnemies = new ArrayList<>();

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.FixedInstance mSpriteTower;

    public TeleportTower() {
        StaticData s = (StaticData)getStaticData();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom(4));

        mSpriteTower = s.spriteTower.yieldStatic(Layers.TOWER);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(getGame().getRandom(4));
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base4, 4);
        s.spriteBase.setMatrix(1f, 1f, null, null);

        s.spriteTower = Sprite.fromResources(R.drawable.teleport_tower, 4);
        s.spriteTower.setMatrix(0.8f, 0.8f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSpriteBase);
        getGame().add(mSpriteTower);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSpriteBase);
        getGame().remove(mSpriteTower);
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
                getGame().add(new TeleportEffect(this, getPosition(), target, getDamage()));
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
                .filter(GameObject.enabled())
                .exclude(mTeleportedEnemies);
    }
}
