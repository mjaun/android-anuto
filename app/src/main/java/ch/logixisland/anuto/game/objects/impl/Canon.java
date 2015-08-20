package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;
import ch.logixisland.anuto.util.math.Vector2;

public class Canon extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;

    private final static float REBOUND_RANGE = 0.25f;
    private final static float REBOUND_DURATION = 0.2f;

    private float mAngle;
    private boolean mReboundActive;

    private final SampledFunction mReboundFunction;

    private final Sprite mSpriteBase;
    private final Sprite mSpriteCanon;

    public Canon() {
        mSpriteBase = Sprite.fromResources(getGame().getResources(), R.drawable.base1, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteCanon = Sprite.fromResources(getGame().getResources(), R.drawable.canon, 4);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setIndex(getGame().getRandom().nextInt(4));
        mSpriteCanon.setMatrix(0.3f, 1.0f, new Vector2(0.15f, 0.4f), -90f);
        mSpriteCanon.setLayer(Layers.TOWER);

        mReboundFunction = Function.sine()
                .multiply(REBOUND_RANGE)
                .stretch(GameEngine.TARGET_FRAME_RATE * REBOUND_DURATION / (float)Math.PI)
                .sample();

        mAngle = 90f;
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
    public void onDraw(Sprite sprite, Canvas canvas) {
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
                Shot shot = new CanonShot(getPosition(), getTarget(), getDamage());
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
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
