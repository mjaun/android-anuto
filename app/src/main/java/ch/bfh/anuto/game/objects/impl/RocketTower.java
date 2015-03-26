package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Sprite;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.util.math.Vector2;

public class RocketTower extends AimingTower {

    private final static int RELOAD_TIME = 20;
    private final static float RANGE = 5f;

    private Sprite mSprite;
    private float mAngle;

    public RocketTower() {
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    public RocketTower(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void init(Resources res) {
        mSprite = Sprite.fromResources(this, res, R.drawable.rocket_tower);
        mSprite.calcMatrix(null, 1f, new Vector2(0.5f, 0.5f));
        mGame.addDrawObject(mSprite, LAYER);
    }

    @Override
    public void clean() {
        mGame.removeDrawObject(mSprite);
    }

    @Override
    public void onBeforeDraw(Sprite sprite, Canvas canvas) {
        super.onBeforeDraw(sprite, canvas);
        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mTarget != null) {
            if (mReloaded) {
                // TODO
            }

            mAngle = getAngleTo(mTarget);
        }
    }
}
