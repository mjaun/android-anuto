package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class RocketLauncher extends AimingTower {

    private final static int VALUE = 300;
    private final static float RELOAD_TIME = 2.5f;
    private final static float RANGE = 2.5f;
    private final static float SHOT_SPAWN_OFFSET = 0.9f;

    private float mAngle;

    private Sprite mSprite;
    private Rocket mRocket;

    public RocketLauncher() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
        mValue = VALUE;
    }

    public RocketLauncher(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void onInit() {
        super.onInit();

        mAngle = mGame.getRandom(360f);

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.rocket_launcher, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom(4));
        mSprite.setMatrix(1f, 1f, null, -90f);
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

        if (mReloaded && mRocket == null) {
            mRocket = new Rocket(mPosition);
            mRocket.setAngle(mAngle);
            mGame.add(mRocket);
        }

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mRocket != null) {
                mRocket.setTarget(mTarget);
                shoot(mRocket);
                mRocket = null;
            }
        }
    }
}
