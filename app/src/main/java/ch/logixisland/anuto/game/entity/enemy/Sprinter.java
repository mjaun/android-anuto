package ch.logixisland.anuto.game.entity.enemy;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.render.AnimatedSprite;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.ReplicatedSprite;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.util.math.function.Function;
import ch.logixisland.anuto.util.math.function.SampledFunction;

public class Sprinter extends Enemy {

    private final static float ANIMATION_SPEED = 0.7f;

    private class StaticData implements Runnable {
        SampledFunction mSpeedFunction;

        SpriteTemplate mSpriteTemplate;
        AnimatedSprite mReferenceSprite;

        @Override
        public void run() {
            mReferenceSprite.tick();
            mSpeedFunction.step();
        }
    }

    private float mAngle;
    private StaticData mStatic;

    public Sprinter() {
        mStatic = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createReplication(mStatic.mReferenceSprite);
        mSprite.setListener(this);
    }

    private ReplicatedSprite mSprite;

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpeedFunction = Function.sine()
                .multiply(getConfigSpeed() * 0.9f)
                .offset(getConfigSpeed() * 0.1f)
                .repeat((float)Math.PI)
                .stretch(GameEngine.TARGET_FRAME_RATE / ANIMATION_SPEED / (float)Math.PI)
                .sample();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.sprinter, 6);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, null);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForwardBackward();
        s.mReferenceSprite.setFrequency(ANIMATION_SPEED);

        getGame().add(s);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);
    }

    @Override
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
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
            setBaseSpeed(mStatic.mSpeedFunction.getValue());
        }
    }
}
