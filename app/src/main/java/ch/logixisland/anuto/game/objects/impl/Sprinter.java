package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Function;
import ch.logixisland.anuto.util.math.SampledFunction;

public class Sprinter extends Enemy {

    private final static float ANIMATION_SPEED = 0.7f;

    private class StaticData extends GameEngine.StaticData {
        public SampledFunction speedFunction;

        public Sprite sprite;
        public Sprite.AnimatedInstance animator;

        @Override
        public void tick() {
            animator.tick();
            speedFunction.step();
        }
    }

    private float mAngle;
    private StaticData mStatic;

    public Sprinter() {
        mStatic = (StaticData)getStaticData();

        mSprite = mStatic.animator.copycat();
        mSprite.setListener(this);
    }

    private Sprite.Instance mSprite;

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.speedFunction = Function.sine()
                .multiply(getConfigSpeed() * 0.9f)
                .offset(getConfigSpeed() * 0.1f)
                .repeat((float)Math.PI)
                .stretch(GameEngine.TARGET_FRAME_RATE / ANIMATION_SPEED / (float)Math.PI)
                .sample();

        s.sprite = Sprite.fromResources(R.drawable.sprinter, 6);
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
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);
    }

    @Override
    public void tick() {
        super.tick();

        if (hasWayPoint()) {
            mAngle = getDirection().angle();
            setBaseSpeed(mStatic.speedFunction.getValue());
        }
    }
}
