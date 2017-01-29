package ch.logixisland.anuto.entity.enemy;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.logic.TickListener;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.AnimatedSprite;
import ch.logixisland.anuto.engine.render.sprite.ReplicatedSprite;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.util.data.EnemyConfig;
import ch.logixisland.anuto.util.math.function.Function;
import ch.logixisland.anuto.util.math.function.SampledFunction;

public class Sprinter extends Enemy {

    private final static float ANIMATION_SPEED = 0.7f;

    private class StaticData implements TickListener {
        SampledFunction mSpeedFunction;

        SpriteTemplate mSpriteTemplate;
        AnimatedSprite mReferenceSprite;

        @Override
        public void tick() {
            mReferenceSprite.tick();
            mSpeedFunction.step();
        }
    }

    private float mAngle;
    private StaticData mStatic;

    public Sprinter(EnemyConfig config) {
        super(config);
        mStatic = (StaticData) getStaticData();

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
                .repeat((float) Math.PI)
                .stretch(GameEngine.TARGET_FRAME_RATE / ANIMATION_SPEED / (float) Math.PI)
                .sample();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.attr.sprinter, 6);
        s.mSpriteTemplate.setMatrix(0.9f, 0.9f, null, null);

        s.mReferenceSprite = getSpriteFactory().createAnimated(Layers.ENEMY, s.mSpriteTemplate);
        s.mReferenceSprite.setSequenceForwardBackward();
        s.mReferenceSprite.setFrequency(ANIMATION_SPEED);

        getGameEngine().add(s);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);
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
