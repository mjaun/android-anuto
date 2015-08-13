package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class Canon extends AimingTower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 0.4f;
    private final static float RANGE = 3.5f;
    private final static float SHOT_SPAWN_OFFSET = 0.9f;

    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private float mAngle;
    private float mRebound;
    private float mReboundValue;
    private Sprite mSpriteBase;
    private Sprite mSpriteCanon;

    public Canon() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;

        mAngle = mGame.getRandom(360f);
    }

    public Canon(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void onInit() {
        super.onInit();

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base1, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f);
        mSpriteBase.setLayer(Layers.TOWER_BASE);
        mGame.add(mSpriteBase);

        mSpriteCanon = Sprite.fromResources(mGame.getResources(), R.drawable.canon, 4);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setIndex(mGame.getRandom().nextInt(4));
        mSpriteCanon.setMatrix(0.3f, 1.0f, new Vector2(0.15f, 0.2f), -90f);
        mSpriteCanon.setLayer(Layers.TOWER);
        mGame.add(mSpriteCanon);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteCanon);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);

        if (sprite == mSpriteCanon && mReboundValue > 0) {
            canvas.translate(-mReboundValue, 0);
        }
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mRebound > 0) {
            mRebound -= Math.PI / GameEngine.TARGET_FRAME_RATE / REBOUND_DURATION;
            mReboundValue = (float)Math.sin(mRebound) * REBOUND_RANGE;
        } else {
            mReboundValue = 0f;
        }

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mReloaded) {
                Shot shot = new CanonShot(mPosition, mTarget);
                shot.move(Vector2.createPolar(SHOT_SPAWN_OFFSET, mAngle));
                shoot(shot);

                mRebound = (float)Math.PI;
            }
        }
    }
}
