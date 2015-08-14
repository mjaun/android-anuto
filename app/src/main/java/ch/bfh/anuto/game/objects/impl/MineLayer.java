package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import java.util.ArrayList;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.game.objects.Tower;

public class MineLayer extends Tower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 1.5f;
    private final static float RANGE = 3.5f;
    private final static int MAX_MINE_COUNT = 3;

    private final static float ANIMATION_SPEED = 2.0f;

    private float mAngle;
    private boolean mShooting;
    private final ArrayList<Mine> mMines = new ArrayList<>();

    private Sprite mSprite;
    private Sprite.Animator mAnimator;

    public MineLayer() {
        mValue = VALUE;
        mRange = RANGE;
        mReloadTime = RELOAD_TIME;
    }

    @Override
    public void onInit() {
        super.onInit();

        mAngle = mGame.getRandom(360f);

        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.catapult, 6);
        mSprite.setListener(this);
        mSprite.setMatrix(1f, 1f, null, null);
        mSprite.setLayer(Layers.TOWER);
        mGame.add(mSprite);

        mAnimator = new Sprite.Animator();
        mAnimator.setSequence(mSprite.sequenceForwardBackward());
        mAnimator.setSpeed(ANIMATION_SPEED);
        mSprite.setAnimator(mAnimator);
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

        if (mReloaded && mMines.size() < MAX_MINE_COUNT) {
            mShooting = true;
        }

        if (mShooting) {
            mSprite.animate();

            if (mAnimator.getPosition() == 5) {
                //Vector2 targetPos = mShotTarget.getPositionAfter2(MortarShot.TIME_TO_TARGET + 1f);
                //shoot(new Mine(mPosition, targetPos));
                mShooting = false;
            }
        }

        if (mAnimator.getPosition() != 0) {
            mSprite.animate();
        }
    }
}
