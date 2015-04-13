package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.util.math.Vector2;

public class BasicTower extends AimingTower {

    private final static int VALUE = 100;
    private final static float RELOAD_TIME = 0.3f;
    private final static float RANGE = 3.5f;
    private final static float SHOT_SPAWN_OFFSET = 0.9f;

    private float mAngle;
    private Sprite mSprite;

    public BasicTower() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public BasicTower(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void onInit() {
        super.onInit();

        mSprite = Sprite.fromResources(this, R.drawable.basic_tower);
        mSprite.calcMatrix(null, 1f, new Vector2(0.5f, 0.5f));
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
            mAngle = getAngleTo(mTarget);

            if (mReloaded) {
                Shot shot = new BasicShot(mPosition, mTarget);
                shot.move(Vector2.createPolar(SHOT_SPAWN_OFFSET, mAngle));
                shoot(shot);
            }
        }
    }
}
