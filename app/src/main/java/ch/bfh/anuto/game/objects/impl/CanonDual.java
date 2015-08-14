package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.SineFunction;
import ch.bfh.anuto.util.math.Vector2;

public class CanonDual extends AimingTower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 0.2f;
    private final static float RANGE = 3.5f;
    private final static float SHOT_SPAWN_OFFSET = 0.9f;

    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private class SubCanon {
        private boolean mReboundActive;
        private SineFunction mReboundFunction;
        private Sprite mSprite;
    }

    private float mAngle;
    private boolean mShoot2;
    private SubCanon[] mCanons;

    private Sprite mSpriteBase;
    private Sprite mSpriteTower;

    public CanonDual() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    @Override
    public void onInit() {
        super.onInit();

        mAngle = mGame.getRandom(360f);

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base1, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);
        mGame.add(mSpriteBase);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.canon_dual, 4);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(mGame.getRandom().nextInt(4));
        mSpriteTower.setMatrix(0.5f, 0.5f, null, -90f);
        mSpriteTower.setLayer(Layers.TOWER_BASE);
        mGame.add(mSpriteTower);

        mCanons = new SubCanon[2];

        mCanons[0] = new SubCanon();
        mCanons[0].mReboundFunction = new SineFunction();
        mCanons[0].mReboundFunction.setProperties(0, (float) Math.PI, REBOUND_RANGE, 0f);
        mCanons[0].mReboundFunction.setSection(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION);
        mCanons[0].mReboundActive = false;

        mCanons[0].mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.canon, 4);
        mCanons[0].mSprite.setListener(this);
        mCanons[0].mSprite.setIndex(mGame.getRandom().nextInt(4));
        mCanons[0].mSprite.setMatrix(0.3f, 1.0f, new Vector2(0.15f, 0.4f), -90f);
        mCanons[0].mSprite.setLayer(Layers.TOWER);
        mGame.add(mCanons[0].mSprite);

        mCanons[1] = new SubCanon();
        mCanons[1].mReboundFunction = new SineFunction();
        mCanons[1].mReboundFunction.setProperties(0, (float)Math.PI, REBOUND_RANGE, 0f);
        mCanons[1].mReboundFunction.setSection(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION);
        mCanons[1].mReboundActive = false;

        mCanons[1].mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.canon, 4);
        mCanons[1].mSprite.setListener(this);
        mCanons[1].mSprite.setIndex(mGame.getRandom().nextInt(4));
        mCanons[1].mSprite.setMatrix(0.3f, 1.0f, new Vector2(-0.15f, 0.4f), -90f);
        mCanons[1].mSprite.setLayer(Layers.TOWER);
        mGame.add(mCanons[0].mSprite);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteTower);

        for (SubCanon c : mCanons) {
            mGame.remove(c.mSprite);
        }

        mCanons = null;
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);

        if (sprite == mCanons[0].mSprite && mCanons[0].mReboundActive) {
            canvas.translate(-mCanons[0].mReboundFunction.getValue(), 0);
        }

        if (sprite == mCanons[1].mSprite && mCanons[1].mReboundActive) {
            canvas.translate(-mCanons[1].mReboundFunction.getValue(), 0);
        }
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mReloaded && !mShoot2) {
                Shot shot = new CanonShot(mPosition, mTarget);
                shot.move(Vector2.createPolar(SHOT_SPAWN_OFFSET, mAngle));
                shot.move(Vector2.createPolar(0.3f, mAngle - 90f));
                shoot(shot);

                mCanons[0].mReboundActive = true;
                mShoot2 = true;
            }

            if (mReloaded && mShoot2) {
                Shot shot = new CanonShot(mPosition, mTarget);
                shot.move(Vector2.createPolar(SHOT_SPAWN_OFFSET, mAngle));
                shot.move(Vector2.createPolar(0.3f, mAngle + 90f));
                shoot(shot);

                mCanons[1].mReboundActive = true;
                mShoot2 = false;
            }
        }

        if (mCanons[0].mReboundActive) {
            if (mCanons[0].mReboundFunction.step()) {
                mCanons[0].mReboundFunction.reset();
                mCanons[0].mReboundActive = false;
            }
        }

        if (mCanons[1].mReboundActive) {
            if (mCanons[1].mReboundFunction.step()) {
                mCanons[1].mReboundFunction.reset();
                mCanons[1].mReboundActive = false;
            }
        }
    }
}
