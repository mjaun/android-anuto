package ch.logixisland.anuto.game.objects.impl;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;

public class Blob extends Enemy {

    private final static float ANIMATION_SPEED = 1.5f;

    private final Sprite mSprite;

    public Blob() {
        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.blob, 9);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        Sprite.Animator animator = new Sprite.Animator();
        animator.setSequence(mSprite.sequenceForward());
        animator.setFrequency(ANIMATION_SPEED);
        mSprite.setAnimator(animator);
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        mSprite.animate();
    }
}
