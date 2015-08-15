package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class LaserTower extends AimingTower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 2.0f;
    private final static float RANGE = 6.5f;
    private final static float LASER_LENGTH = 1000f;
    private final static float LASER_SPAWN_OFFSET = 0.7f;

    private float mAngle;

    private final Sprite mSpriteBase;
    private final Sprite mSpriteTower;

    public LaserTower() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;

        mAngle = 90f;

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base5, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteBase.setMatrix(1f, 1f, new Vector2(0.5f, 0.5f), null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.laser_tower3, 4);
        mSpriteTower.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteTower.setMatrix(0.4f, 1f, new Vector2(0.2f, 0.3f), -90f);
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
                Vector2 laserFrom = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(mPosition);
                Vector2 laserTo = Vector2.polar(LASER_LENGTH, mAngle).add(mPosition);
                mGame.add(new LaserEffect(laserFrom, laserTo));
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
