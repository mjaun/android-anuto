package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.util.Vector2;

public class LaserTower extends AimingTower {

    private final static float RELOAD_TIME = 2.0f;
    private final static float RANGE = 5f;

    private Sprite mSprite;
    private float mAngle;

    public LaserTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public LaserTower(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void init(Resources res) {
        super.init(res);

        mSprite = Sprite.fromResources(this, res, R.drawable.laser_tower);
        mSprite.calcMatrix(null, 1f, new Vector2(0.5f, 0.5f));
        mGame.addDrawObject(mSprite, LAYER);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mSprite);
    }

    @Override
    public void beforeDraw(Sprite sprite, Canvas canvas) {
        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mTarget == null) {
            nextTarget();
        }

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mReloaded) {
                Vector2 direction = Vector2.createPolar(1f, mAngle);
                shoot(new LaserEffect(mPosition, direction));
            }
        }
    }
}
