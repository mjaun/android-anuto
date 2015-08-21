package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;

public class Flyer extends Enemy {

    private final static float ANIMATION_SPEED = 1.0f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
        public Sprite.AnimatedInstance animator;

        @Override
        public void tick() {
            animator.tick();
        }
    }

    private float mAngle;

    private Sprite.Instance mSprite;

    public Flyer() {
        StaticData s = (StaticData)getStaticData();

        mSprite = s.animator.copycat();
        mSprite.setListener(this);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.flyer, 6);
        s.sprite.setMatrix(0.9f, 0.9f, null, -90f);

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

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasWayPoint()) {
            mAngle = getDirection().angle();
        }
    }
}
