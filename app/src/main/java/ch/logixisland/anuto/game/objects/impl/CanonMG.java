package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class CanonMG extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;

    private final static float MG_ROTATION_SPEED = 2f;

    private float mAngle;
    private Sprite mSpriteBase;
    private Sprite mSpriteCanon;

    public CanonMG() {
        mAngle = 90f;

        mSpriteBase = Sprite.fromResources(getGame().getResources(), R.drawable.base1, 4);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom().nextInt(4));
        mSpriteBase.setMatrix(1f, 1f, null, null);
        mSpriteBase.setLayer(Layers.TOWER_BASE);

        mSpriteCanon = Sprite.fromResources(getGame().getResources(), R.drawable.canon_mg, 5);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setMatrix(0.8f, 1.0f, new Vector2(0.4f, 0.4f), -90f);
        mSpriteCanon.setLayer(Layers.TOWER);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSpriteCanon.sequenceForward());
        animator.setFrequency(MG_ROTATION_SPEED);
        mSpriteCanon.setAnimator(animator);
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
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());
            mSpriteCanon.animate();

            if (isReloaded()) {
                Shot shot = new CanonShotMG(getPosition(), getDirectionTo(getTarget()));
                shot.move(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
                getGame().add(shot);

                setReloaded(false);
            }
        }
    }

    @Override
    public void drawPreview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
