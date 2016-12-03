package ch.logixisland.anuto.game.entity.shot;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.entity.enemy.Enemy;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.Random;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class CanonShot extends HomingShot {

    private final static float MOVEMENT_SPEED = 4.0f;
    private final static float ROTATION_SPEED = 1.0f;
    private final static float ROTATION_STEP = ROTATION_SPEED * 360f / GameEngine.TARGET_FRAME_RATE;

    private class StaticData {
        public SpriteTemplate mSpriteTemplate;
    }

    private float mAngle;
    private float mDamage;

    private StaticSprite mSprite;

    public CanonShot(Entity origin, Vector2 position, Enemy target, float damage) {
        super(origin);
        setPosition(position);
        setTarget(target);
        setSpeed(MOVEMENT_SPEED);

        mDamage = damage;

        StaticData s = (StaticData)getStaticData();

        mSprite = getSpriteFactory().createStatic(Layers.SHOT, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setIndex(Random.next(4));
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.canon_shot, 4);
        s.mSpriteTemplate.setMatrix(0.33f, 0.33f, null, null);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSprite);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSprite);
    }

    @Override
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        setDirection(getDirectionTo(getTarget()));
        mAngle += ROTATION_STEP;

        super.tick();
    }

    @Override
    protected void onTargetLost() {
        this.remove();
    }

    @Override
    protected void onTargetReached() {
        getTarget().damage(mDamage, getOrigin());
        this.remove();
    }
}
