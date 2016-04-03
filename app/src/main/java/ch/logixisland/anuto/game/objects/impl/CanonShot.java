package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;
import ch.logixisland.anuto.game.objects.GameObject;
import ch.logixisland.anuto.game.objects.HomingShot;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class CanonShot extends HomingShot {

    private final static float MOVEMENT_SPEED = 4.0f;
    private final static float ROTATION_SPEED = 1.0f;
    private final static float ROTATION_STEP = ROTATION_SPEED * 360f / GameEngine.TARGET_FRAME_RATE;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
    }

    private float mAngle;
    private float mDamage;

    private Sprite.FixedInstance mSprite;

    public CanonShot(GameObject origin, Vector2 position, Enemy target, float damage) {
        super(origin);
        setPosition(position);
        setTarget(target);
        setSpeed(MOVEMENT_SPEED);

        mDamage = damage;

        StaticData s = (StaticData)getStaticData();

        mSprite = s.sprite.yieldStatic(Layers.SHOT);
        mSprite.setListener(this);
        mSprite.setIndex(getGame().getRandom(4));
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.canon_shot, 4);
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
    public void onDraw(DrawObject sprite, Canvas canvas) {
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
