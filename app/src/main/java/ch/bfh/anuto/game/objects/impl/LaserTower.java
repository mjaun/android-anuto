package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.util.math.Vector2;

public class LaserTower extends AimingTower {

    private final static int VALUE = 700;
    private final static float RELOAD_TIME = 2.0f;
    private final static float RANGE = 6.5f;
    private final static float LASER_LENGTH = 1000f;
    private final static float LASER_SPAWN_OFFSET = 0.8f;

    private Sprite mSprite;
    private float mAngle;

    public LaserTower() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public LaserTower(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.laser_tower);
        mSprite.setListener(this);
        mSprite.setMatrix(null, 1f, new Vector2(0.5f, 0.5f));
        mSprite.setLayer(Layers.TOWER);
        mGame.add(mSprite);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSprite);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mTarget == null) {
            nextTarget();
        }

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mReloaded) {
                Vector2 laserFrom = Vector2.createPolar(LASER_SPAWN_OFFSET, mAngle).add(mPosition);
                Vector2 laserTo = Vector2.createPolar(LASER_LENGTH, mAngle).add(mPosition);
                shoot(new LaserEffect(laserFrom, laserTo));
            }
        }
    }
}
