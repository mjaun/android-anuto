package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.util.math.Vector2;

public class RocketTower extends AimingTower {

    private final static int VALUE = 300;
    private final static float RELOAD_TIME = 3f;
    private final static float RANGE = 2.5f;
    private final static float SHOT_SPAWN_OFFSET = 0.9f;

    private Sprite mSprite;
    private float mAngle;

    public RocketTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
        mValue = VALUE;
    }

    public RocketTower(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(this, R.drawable.rocket_tower);
        mSprite.calcMatrix(null, 1.5f, new Vector2(0.45f, 0.75f));
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

        if (mTarget != null) {
            if (mReloaded) {
                Shot shot = new RocketShot(mPosition, mTarget);
                shot.move(Vector2.createPolar(SHOT_SPAWN_OFFSET, mAngle));
                shoot(shot);
            }

            mAngle = getAngleTo(mTarget);
        }
    }
}
