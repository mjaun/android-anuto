package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Sprite;

public class RocketLauncher extends AimingTower {

    private final static float ROCKET_LOAD_TIME = 1.0f;

    private float mAngle;
    private Rocket mRocket;
    private TickTimer mRocketLoadTimer;

    private Sprite mSprite;
    private Sprite mRocketSprite; // used for preview only

    public RocketLauncher() {
        mAngle = 90f;
        mRocketLoadTimer = TickTimer.createInterval(ROCKET_LOAD_TIME);

        mSprite = Sprite.fromResources(getGame().getResources(), R.drawable.rocket_launcher, 4);
        mSprite.setListener(this);
        mSprite.setIndex(getGame().getRandom(4));
        mSprite.setMatrix(1.1f, 1.1f, null, -90f);
        mSprite.setLayer(Layers.TOWER);
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);

        if (mRocket != null) {
            getGame().remove(mRocket);
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
            mRocket = new Rocket(getPosition(), getDamage());
            mRocket.setAngle(mAngle);
            getGame().add(mRocket);
        }

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (mRocket != null) {
                mRocket.setAngle(mAngle);

                if (isReloaded()) {
                    mRocket.setTarget(getTarget());
                    mRocket.setEnabled(true);
                    mRocket = null;

                    setReloaded(false);
                }
            }
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        if (mRocketSprite == null) {
            mRocketSprite = Sprite.fromResources(getGame().getResources(), R.drawable.rocket, 4);
            mRocketSprite.setListener(this);
            mRocketSprite.setIndex(getGame().getRandom(4));
            mRocketSprite.setMatrix(0.8f, 1f, null, -90f);
        }

        mSprite.draw(canvas);
        mRocketSprite.draw(canvas);
    }
}
