package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.SineFunction;
import ch.logixisland.anuto.util.math.Vector2;

public class CanonDual extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;

    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private class SubCanon {
        boolean reboundActive;
        SineFunction reboundFunction;
        Sprite sprite;
    }

    private float mAngle;
    private boolean mShoot2;
    private SubCanon[] mCanons;

    private Sprite mSpriteBase;
    private Sprite mSpriteTower;

    public CanonDual() {
        mAngle = 90f;

        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base1, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteTower = Sprite.fromResources(mGame.getResources(), R.drawable.canon_dual, 4);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(mGame.getRandom().nextInt(4));
        mSpriteTower.setMatrix(0.5f, 0.5f, null, -90f);
        mSpriteTower.setLayer(Layers.TOWER_BASE);

        mCanons = new SubCanon[2];

        mCanons[0] = new SubCanon();
        mCanons[0].reboundFunction = new SineFunction();
        mCanons[0].reboundFunction.setProperties(0, (float) Math.PI, REBOUND_RANGE, 0f);
        mCanons[0].reboundFunction.setSection(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION);
        mCanons[0].reboundActive = false;

        mCanons[0].sprite = Sprite.fromResources(mGame.getResources(), R.drawable.canon, 4);
        mCanons[0].sprite.setListener(this);
        mCanons[0].sprite.setIndex(mGame.getRandom().nextInt(4));
        mCanons[0].sprite.setMatrix(0.3f, 1.0f, new Vector2(0.45f, 0.4f), -90f);
        mCanons[0].sprite.setLayer(Layers.TOWER);

        mCanons[1] = new SubCanon();
        mCanons[1].reboundFunction = new SineFunction();
        mCanons[1].reboundFunction.setProperties(0, (float) Math.PI, REBOUND_RANGE, 0f);
        mCanons[1].reboundFunction.setSection(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION);
        mCanons[1].reboundActive = false;

        mCanons[1].sprite = Sprite.fromResources(mGame.getResources(), R.drawable.canon, 4);
        mCanons[1].sprite.setListener(this);
        mCanons[1].sprite.setIndex(mGame.getRandom().nextInt(4));
        mCanons[1].sprite.setMatrix(0.3f, 1.0f, new Vector2(-0.15f, 0.4f), -90f);
        mCanons[1].sprite.setLayer(Layers.TOWER);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSpriteBase);
        mGame.add(mSpriteTower);
        mGame.add(mCanons[0].sprite);
        mGame.add(mCanons[1].sprite);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteTower);

        for (SubCanon c : mCanons) {
            mGame.remove(c.sprite);
        }

        mCanons = null;
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);

        if (sprite == mCanons[0].sprite && mCanons[0].reboundActive) {
            canvas.translate(-mCanons[0].reboundFunction.getValue(), 0);
        }

        if (sprite == mCanons[1].sprite && mCanons[1].reboundActive) {
            canvas.translate(-mCanons[1].reboundFunction.getValue(), 0);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (mTarget != null) {
            mAngle = getAngleTo(mTarget);

            if (mReloaded) {
                if (!mShoot2) {
                    Shot shot = new CanonShot(mPosition, mTarget, mConfig.damage);
                    shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                    shot.move(Vector2.polar(0.3f, mAngle + 90f));
                    mGame.add(shot);

                    mReloaded = false;
                    mCanons[0].reboundActive = true;
                    mShoot2 = true;
                } else {
                    Shot shot = new CanonShot(mPosition, mTarget, mConfig.damage);
                    shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                    shot.move(Vector2.polar(0.3f, mAngle - 90f));
                    mGame.add(shot);

                    mReloaded = false;
                    mCanons[1].reboundActive = true;
                    mShoot2 = false;
                }
            }
        }

        if (mCanons[0].reboundActive) {
            if (mCanons[0].reboundFunction.step()) {
                mCanons[0].reboundFunction.reset();
                mCanons[0].reboundActive = false;
            }
        }

        if (mCanons[1].reboundActive) {
            if (mCanons[1].reboundFunction.step()) {
                mCanons[1].reboundFunction.reset();
                mCanons[1].reboundActive = false;
            }
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
        mCanons[0].sprite.draw(canvas);
        mCanons[1].sprite.draw(canvas);
    }
}
