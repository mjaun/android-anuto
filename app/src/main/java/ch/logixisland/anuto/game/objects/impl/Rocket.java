package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.HomingShot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class Rocket extends HomingShot {

    private final static float EXPLOSION_RADIUS = 1.5f;

    private final static float MOVEMENT_SPEED = 2.5f;
    private final static float ANIMATION_SPEED = 3f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
        public Sprite spriteFire;
    }

    private float mDamage;
    private float mAngle;

    private Sprite.FixedInstance mSprite;
    private Sprite.AnimatedInstance mSpriteFire;

    public Rocket(Vector2 position, float damage) {
        setPosition(position);
        setEnabled(false);

        mSpeed = MOVEMENT_SPEED;
        mDamage = damage;

        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldStatic(Layers.SHOT);
        mSprite.setListener(this);
        mSprite.setIndex(getGame().getRandom(4));

        mSpriteFire = s.spriteFire.yieldAnimated(Layers.SHOT);
        mSpriteFire.setListener(this);
        mSpriteFire.setSequence(mSpriteFire.sequenceForward());
        mSpriteFire.setFrequency(ANIMATION_SPEED);
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.rocket, 4);
        s.sprite.setMatrix(0.8f, 1f, null, -90f);

        s.spriteFire = Sprite.fromResources(R.drawable.rocket_fire, 4);
        s.spriteFire.setMatrix(0.3f, 0.3f, new Vector2(0.15f, 0.6f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSprite);

        if (isEnabled()) {
            getGame().add(mSpriteFire);
        }
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSprite);

        if (isEnabled()) {
            getGame().remove(mSpriteFire);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (isInGame() && !enabled) {
            getGame().remove(mSpriteFire);
        }

        if (isInGame() && enabled) {
            getGame().add(mSpriteFire);
        }
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        if (isEnabled()) {
            mDirection = getDirectionTo(mTarget);
            mAngle = mDirection.angle();

            mSpriteFire.tick();
        }

        super.tick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy) getGame().get(Enemy.TYPE_ID)
                .min(distanceTo(getPosition()));

        if (closest == null) {
            getGame().remove(this);
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        getGame().add(new Explosion(mTarget.getPosition(), mDamage, EXPLOSION_RADIUS));
        getGame().remove(this);
    }
}
