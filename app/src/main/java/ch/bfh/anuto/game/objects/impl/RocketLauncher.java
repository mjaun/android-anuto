package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.TickTimer;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Sprite;

public class RocketLauncher extends AimingTower {

    private final static int VALUE = 200;
    private final static float ROCKET_LOAD_TIME = 1.0f;
    private final static float RELOAD_TIME = 2.5f;
    private final static float RANGE = 2.5f;

    private float mAngle;
    private Rocket mRocket;
    private TickTimer mRocketLoadTimer;

    private Sprite mSprite;
    private Sprite mRocketSprite; // used for preview only

    public RocketLauncher() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
        mValue = VALUE;

        mAngle = 90f;
        mRocketLoadTimer = TickTimer.createInterval(ROCKET_LOAD_TIME);

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.rocket_launcher, 4);
        mSprite.setListener(this);
        mSprite.setIndex(mGame.getRandom(4));
        mSprite.setMatrix(1f, 1f, null, -90f);
        mSprite.setLayer(Layers.TOWER);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);

        if (mRocket != null) {
            mGame.remove(mRocket);
        }
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }
    @Override
    public void tick() {
        super.tick();

        if (mRocket == null && mRocketLoadTimer.tick()) {
            mRocket = new Rocket(mPosition);
            mRocket.setAngle(mAngle);
            mGame.add(mRocket);
        }

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mRocket != null) {
                mRocket.setAngle(mAngle);

                if (mReloaded) {
                    mRocket.setTarget(mTarget);
                    mRocket.setEnabled(true);
                    mRocket = null;

                    mReloaded = false;
                }
            }
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        if (mRocketSprite == null) {
            mRocketSprite = Sprite.fromResources(mGame.getResources(), R.drawable.rocket_shot, 4);
            mRocketSprite.setListener(this);
            mRocketSprite.setIndex(mGame.getRandom(4));
            mRocketSprite.setMatrix(0.8f, 1f, null, -90f);
        }

        mSprite.draw(canvas);
        mRocketSprite.draw(canvas);
    }
}
