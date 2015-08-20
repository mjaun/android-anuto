package ch.logixisland.anuto.game.objects.impl;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Shot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueShot extends Shot {

    private final static float MOVEMENT_SPEED = 4.0f;
    private final static float ANIMATION_SPEED = 1.0f;

    private float mSpeedModifier;
    private Vector2 mTarget;

    private final Sprite mSprite;

    public GlueShot(Vector2 position, Vector2 target, float speedModifier) {
        setPosition(position);
        mTarget = new Vector2(target);

        mSprite = Sprite.fromResources(getGame().getResources(), R.drawable.glue_shot, 6);
        mSprite.setListener(this);
        mSprite.setMatrix(0.33f, 0.33f, null, null);
        mSprite.setLayer(Layers.SHOT);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSprite.sequenceForward());
        animator.setFrequency(ANIMATION_SPEED);
        mSprite.setAnimator(animator);

        mSpeed = MOVEMENT_SPEED;
        mDirection = getDirectionTo(target);
        mSpeedModifier = speedModifier;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        mSprite.animate();

        if (getDistanceTo(mTarget) < mSpeed / GameEngine.TARGET_FRAME_RATE) {
            getGame().add(new GlueEffect(mTarget, mSpeedModifier));
            this.remove();
        }
    }
}
