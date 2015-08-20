package ch.logixisland.anuto.game.objects.impl;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;

public class Soldier extends Enemy {

    private final static float ANIMATION_SPEED = 1f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
        public Sprite.AnimatedInstance animator;

        @Override
        public void tick() {
            animator.tick();
        }
    }

    private Sprite.Instance mSprite;

    public Soldier() {
        StaticData s = (StaticData)getStaticData();

        mSprite = s.animator.copycat();
        mSprite.setListener(this);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.soldier, 12);
        s.sprite.setMatrix(0.9f, 0.9f, null, null);

        s.animator = s.sprite.yieldAnimated(Layers.ENEMY);
        s.animator.setSequence(s.animator.sequenceForwardBackward());
        s.animator.setFrequency(ANIMATION_SPEED);

        return s;
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
}
