package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class Mortar extends AimingTower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 1.5f;
    private final static float RANGE = 3.5f;

    private final static float SHOT_SPAWN_OFFSET = 0.6f;
    private final static float REBOUND_DURATION = 0.2f;

    private float mAngle;
    private boolean mRebounding;

    private Sprite mSpriteBase;
    private Sprite mSpriteCanon;

    private Sprite.Animator mAnimator;

    public Mortar() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    @Override
    public void onInit() {
        super.onInit();

        mAngle = mGame.getRandom(360f);

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base2, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);
        mGame.add(mSpriteBase);

        mSpriteCanon = Sprite.fromResources(mGame.getResources(), R.drawable.mortar2, 8);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setMatrix(0.8f, null, new Vector2(0.4f, 0.2f), -90f);
        mSpriteCanon.setLayer(Layers.TOWER);
        mGame.add(mSpriteCanon);

        mAnimator = new Sprite.Animator();
        mAnimator.setSequence(mSpriteCanon.sequenceForwardBackward());
        mAnimator.setSpeed(1f / REBOUND_DURATION);
        mSpriteCanon.setAnimator(mAnimator);
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

        if (sprite == mSpriteCanon) {
            canvas.rotate(mAngle);
        }
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mTarget != null && mReloaded) {
            Vector2 targetPos = mTarget.getPositionAfter2(Mine.TIME_TO_TARGET + 1f);
            mAngle = getAngleTo(targetPos);

            Vector2 shotPos = mPosition.copy().add(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
            Shot shot = new MortarShot(shotPos, targetPos);
            shoot(shot);

            mRebounding = true;
        }

        if (mRebounding && mSpriteCanon.animate()) {
            mRebounding = false;
        } else if (mAnimator.getPosition() != 0) {
            mSpriteCanon.animate();
        }
    }
}
