package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Sprite;

public class TeleportTower extends AimingTower {

    private final Sprite mSpriteBase;
    private final Sprite mSpriteTower;

    public TeleportTower() {
        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base4, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.teleport_tower, 4);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(mGame.getRandom().nextInt(4));
        mSpriteTower.setMatrix(0.8f, 0.8f, null, null);
        mSpriteTower.setLayer(Layers.TOWER);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSpriteBase);
        mGame.add(mSpriteTower);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteTower);
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloaded && mTarget != null) {
            if (!mTarget.isEnabled() || getDistanceTo(mTarget) > mConfig.range) {
                setTarget(null);
            } else {
                mGame.add(new TeleportEffect(mPosition, mTarget, mConfig.damage));
                mReloaded = false;
            }
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }
}
