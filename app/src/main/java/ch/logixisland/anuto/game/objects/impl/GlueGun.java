package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueGun extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;
    private final static float REBOUND_DURATION = 0.5f;

    private float mAngle;
    private boolean mRebounding;

    private final Sprite mSpriteBase;
    private final Sprite mSpriteCanon;

    public GlueGun() {
        mSpriteBase = Sprite.fromResources(mGame.getResources(), R.drawable.base4, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(mGame.getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteCanon = Sprite.fromResources(mGame.getResources(), R.drawable.glue_gun, 6);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setMatrix(0.8f, 1.0f, new Vector2(0.4f, 0.4f), -90f);
        mSpriteCanon.setLayer(Layers.TOWER);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSpriteCanon.sequenceForwardBackward());
        animator.setInterval(REBOUND_DURATION);
        mSpriteCanon.setAnimator(animator);

        mAngle = 90f;
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSpriteBase);
        mGame.add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSpriteBase);
        mGame.remove(mSpriteCanon);
    }

    @Override
    public void onDraw(Sprite sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mReloaded && getTarget() != null) {
            Vector2 target = getTarget().getPositionAfter(1.0f);

            mAngle = getAngleTo(target);

            Shot shot = new GlueShot(mPosition, target, 1f / getDamage());
            shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
            mGame.add(shot);

            mReloaded = false;
            mRebounding = true;
        }

        if (mRebounding && mSpriteCanon.animate()) {
            mRebounding = false;
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
