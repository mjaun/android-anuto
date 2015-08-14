package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.bfh.anuto.R;
import ch.bfh.anuto.game.GameEngine;
import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.AimingTower;
import ch.bfh.anuto.game.objects.Shot;
import ch.bfh.anuto.game.objects.Sprite;
import ch.bfh.anuto.util.math.Vector2;

public class CanonMG extends AimingTower {

    private final static int VALUE = 200;
    private final static float RELOAD_TIME = 0.1f;
    private final static float RANGE = 3.5f;
    private final static float SHOT_SPAWN_OFFSET = 0.7f;

    private final static float MG_ROTATION_SPEED = 10f;

    private float mAngle;
    private Sprite mSpriteBase;
    private Sprite mSpriteTower;
    private Sprite mSpriteCanon;

    public CanonMG() {
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

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.canon_mg_base, 4);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(mGame.getRandom().nextInt(4));
        mSpriteTower.setMatrix(0.7f, 0.7f, new Vector2(0.35f, 0.45f), -90f);
        mSpriteTower.setLayer(Layers.TOWER);
        mGame.add(mSpriteTower);

        mSpriteCanon = Sprite.fromResources(mGame.getResources(), R.drawable.canon_mg_gun, 5);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setMatrix(0.8f, 0.9f, new Vector2(0.4f, 0.25f), -90f);
        mSpriteCanon.setLayer(Layers.TOWER);
        mGame.add(mSpriteCanon);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSpriteCanon.sequenceForward());
        animator.setSpeed(MG_ROTATION_SPEED);
        mSpriteCanon.setAnimator(animator);
    }

    @Override
    public void onClean() {
        super.onClean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteTower);
        mGame.remove(mSpriteCanon);
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
            mSpriteCanon.animate();

            if (mReloaded) {
                Shot shot = new CanonShotMG(mPosition, getDirectionTo(mTarget));
                shot.move(Vector2.createPolar(SHOT_SPAWN_OFFSET, mAngle));
                shoot(shot);
            }
        }
    }
}
