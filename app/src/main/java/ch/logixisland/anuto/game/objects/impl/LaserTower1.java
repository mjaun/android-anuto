package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class LaserTower1 extends AimingTower {

    private final static float LASER_SPAWN_OFFSET = 0.7f;

    private float mAngle;

    private final Sprite mSpriteBase;
    private final Sprite mSpriteTower;

    public LaserTower1() {
        mAngle = 90f;

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base5, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteBase.setMatrix(1f, 1f, null, -90f);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.laser_tower1, 4);
        mSpriteTower.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteTower.setMatrix(0.4f, 0.9f, new Vector2(0.2f, 0.2f), -90f);
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
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mReloaded) {
                Vector2 origin = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(mPosition);
                mGame.add(new Laser(origin, mTarget, mConfig.damage, 0));
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
