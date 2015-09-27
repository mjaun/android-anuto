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

public class Canon extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;
    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteCanon;
    }

    private float mAngle = 90f;
    private boolean mReboundActive;

    private SampledFunction mReboundFunction;

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.FixedInstance mSpriteCanon;

    public Canon() {
        StaticData s = (StaticData)getStaticData();

        mReboundFunction = Function.sine()
                .multiply(REBOUND_RANGE)
                .stretch(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION / (float)Math.PI)
                .sample();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom(4));

        mSpriteCanon = s.spriteCanon.yieldStatic(Layers.TOWER);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setIndex(getGame().getRandom(4));
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base1, 4);
        s.spriteBase.setMatrix(1f, 1f, null, null);

        s.spriteCanon = Sprite.fromResources(R.drawable.canon, 4);
        s.spriteCanon.setMatrix(0.4f, 1.0f, new Vector2(0.2f, 0.2f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSpriteBase);
        getGame().add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSpriteBase);
        getGame().remove(mSpriteCanon);
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);

        if (sprite == mSpriteCanon && mReboundActive) {
            canvas.translate(-mReboundFunction.getValue(), 0);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Shot shot = new CanonShot(this, getPosition(), getTarget(), getDamage());
                shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                getGame().add(shot);

                setReloaded(false);
                mReboundActive = true;
            }
        }

        if (mReboundActive) {
            mReboundFunction.step();
            if (mReboundFunction.getPosition() >= GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION) {
                mReboundFunction.reset();
                mReboundActive = false;
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
