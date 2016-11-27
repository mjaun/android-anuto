package ch.logixisland.anuto.game.entity.shot;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.effect.GlueEffect;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.render.Sprite;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class GlueShot extends Shot {

    public final static float MOVEMENT_SPEED = 4.0f;
    private final static float ANIMATION_SPEED = 1.0f;

    private class StaticData {
        public Sprite sprite;
    }

    private float mSpeedModifier;
    private float mDuration;
    private Vector2 mTarget;

    private Sprite.AnimatedInstance mSprite;

    public GlueShot(Entity origin, Vector2 position, Vector2 target, float speedModifier, float duration) {
        super(origin);
        setPosition(position);
        mTarget = new Vector2(target);

        setSpeed(MOVEMENT_SPEED);
        setDirection(getDirectionTo(target));

        mSpeedModifier = speedModifier;
        mDuration = duration;

        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldAnimated(Layers.SHOT);
        mSprite.setListener(this);
        mSprite.setSequence(mSprite.sequenceForward());
        mSprite.setFrequency(ANIMATION_SPEED);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.glue_shot, 6);
        s.sprite.setMatrix(0.33f, 0.33f, null, null);

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
    public void tick() {
        super.tick();

        mSprite.tick();

        if (getDistanceTo(mTarget) < getSpeed() / GameEngine.TARGET_FRAME_RATE) {
            getGame().add(new GlueEffect(getOrigin(), mTarget, mSpeedModifier, mDuration));
            this.remove();
        }
    }
}
