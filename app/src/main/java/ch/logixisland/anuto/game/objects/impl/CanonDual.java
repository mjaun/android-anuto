package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;
import ch.logixisland.anuto.util.math.Vector2;

public class CanonDual extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;
    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteTower;
        public Sprite spriteCanon;
    }

    private class SubCanon {
        boolean reboundActive;
        SampledFunction reboundFunction;
        Sprite.FixedInstance sprite;
    }

    private float mAngle = 90f;
    private boolean mShoot2 = false;
    private SubCanon[] mCanons = new SubCanon[2];

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.FixedInstance mSpriteTower;

    public CanonDual() {
        StaticData s = (StaticData)getStaticData();

        Function reboundFunction = Function.sine()
                .multiply(REBOUND_RANGE)
                .stretch(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION / (float)Math.PI);

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom().nextInt(4));

        mSpriteTower = s.spriteTower.yieldStatic(Layers.TOWER_LOWER);
        mSpriteTower.setListener(this);
        mSpriteTower.setIndex(getGame().getRandom().nextInt(4));

        for (int i = 0; i < mCanons.length; i++) {
            mCanons[i] = new SubCanon();
            mCanons[i].reboundFunction = reboundFunction.sample();
            mCanons[i].reboundActive = false;

            mCanons[i].sprite = s.spriteCanon.yieldStatic(Layers.TOWER);
            mCanons[i].sprite.setListener(this);
            mCanons[i].sprite.setIndex(getGame().getRandom().nextInt(4));
        }
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base1, 4);
        s.spriteBase.setMatrix(1f, 1f, null, null);

        s.spriteTower = Sprite.fromResources(R.drawable.canon_dual, 4);
        s.spriteTower.setMatrix(0.5f, 0.5f, null, -90f);

        s.spriteCanon = Sprite.fromResources(R.drawable.canon, 4);
        s.spriteCanon.setMatrix(0.3f, 1.0f, new Vector2(0.15f, 0.4f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSpriteBase);
        getGame().add(mSpriteTower);

        for (SubCanon c : mCanons) {
            getGame().add(c.sprite);
        }
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSpriteBase);
        getGame().remove(mSpriteTower);

        for (SubCanon c : mCanons) {
            getGame().remove(c.sprite);
        }

        mCanons = null;
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);

        if (sprite == mCanons[0].sprite) {
            canvas.translate(0, 0.3f);

            if (mCanons[0].reboundActive) {
                canvas.translate(-mCanons[0].reboundFunction.getValue(), 0);
            }
        }

        if (sprite == mCanons[1].sprite) {
            canvas.translate(0, -0.3f);

            if (mCanons[1].reboundActive) {
                canvas.translate(-mCanons[1].reboundFunction.getValue(), 0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                if (!mShoot2) {
                    Shot shot = new CanonShot(this, getPosition(), getTarget(), getDamage());
                    shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                    shot.move(Vector2.polar(0.3f, mAngle + 90f));
                    getGame().add(shot);

                    setReloaded(false);
                    mCanons[0].reboundActive = true;
                    mShoot2 = true;
                } else {
                    Shot shot = new CanonShot(this, getPosition(), getTarget(), getDamage());
                    shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                    shot.move(Vector2.polar(0.3f, mAngle - 90f));
                    getGame().add(shot);

                    setReloaded(false);
                    mCanons[1].reboundActive = true;
                    mShoot2 = false;
                }
            }
        }

        if (mCanons[0].reboundActive) {
            mCanons[0].reboundFunction.step();
            if (mCanons[0].reboundFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION) {
                mCanons[0].reboundFunction.reset();
                mCanons[0].reboundActive = false;
            }
        }

        if (mCanons[1].reboundActive) {
            mCanons[1].reboundFunction.step();
            if (mCanons[1].reboundFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION) {
                mCanons[1].reboundFunction.reset();
                mCanons[1].reboundActive = false;
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteTower.draw(canvas);
        mCanons[0].sprite.draw(canvas);
        mCanons[1].sprite.draw(canvas);
    }
}
