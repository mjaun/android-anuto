package ch.bfh.anuto.game.objects.impl;

import android.content.res.Resources;
import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
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
    public void init() {
        super.init();

        mSprite = Sprite.fromResources(this, R.drawable.rocket_tower);
        mSprite.calcMatrix(null, 1f, new Vector2(0.5f, 0.5f));
        mSprite.setLayer(Layers.TOWER);
        mGame.add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();
        mGame.remove(mSprite);
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
            if (mReloaded) {
                // TODO
            }

            mAngle = getAngleTo(mTarget);
        }
    }
}
