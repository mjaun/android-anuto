package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class LaserTower1 extends AimingTower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 0.1f;
    private final static float RANGE = 6.5f;

    private final static float LASER_OFFSET = 0.6f;

    private float mAngle;

    private Laser1 mLaser;

    private final Sprite mSpriteBase;
    private final Sprite mSpriteTower;

    public LaserTower1() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;

        mAngle = 90f;

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base5, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteBase.setMatrix(1.1f, 1.1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.laser_tower1, 4);
        mSpriteTower.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom(4));
        mSpriteTower.setMatrix(0.4f, 0.8f, new Vector2(0.2f, 0.2f), -90f);
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
            mLaser.setPosition(mPosition.copy().add(Vector2.polar(LASER_OFFSET, mAngle)));
        }
    }

    @Override
    protected void setTarget(Enemy target) {
        super.setTarget(target);

        if (mLaser != null) {
            mLaser.remove();
        }

        if (target != null) {
            mLaser = new Laser1(null, target);
            mGame.add(mLaser);
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
    }
}
