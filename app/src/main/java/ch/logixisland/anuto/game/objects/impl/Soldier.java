package ch.logixisland.anuto.game.objects.impl;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;

public class Soldier extends Enemy {

    private final static float ANIMATION_SPEED = 1f;

    private static Sprite.Animator sAnimator;

    private final Sprite mSprite;

    public Soldier() {
        mSprite = Sprite.fromResources(mGame.getResources(), R.drawable.soldier, 12);
        mSprite.setListener(this);
        mSprite.setMatrix(0.9f, 0.9f, null, null);
        mSprite.setLayer(Layers.ENEMY);

        if (sAnimator == null) {
            sAnimator = new Sprite.SynchronizedAnimator();
            sAnimator.setSequence(mSprite.sequenceForwardBackward());
            sAnimator.setFrequency(ANIMATION_SPEED);
        }

        mSprite.setAnimator(sAnimator);
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
