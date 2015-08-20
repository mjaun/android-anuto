package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Sprite;

public class TeleportTower extends AimingTower {

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteTower;
    }

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

        if (isReloaded() && getTarget() != null) {
            if (!getTarget().isEnabled() || getDistanceTo(getTarget()) > getRange()) {
                setTarget(null);
            } else {
                getGame().add(new TeleportEffect(getPosition(), getTarget(), getDamage()));
                setReloaded(false);
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }
}
