package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class GlueGun extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.7f;
    private final static float REBOUND_DURATION = 0.5f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteCanon;
    }

    private float mGlueDuration;
    private float mAngle = 90f;
    private boolean mRebounding = false;

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.AnimatedInstance mSpriteCanon;

    public GlueGun() {
        mGlueDuration = getProperty("glueDuration");

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setListener(this);
        mSpriteBase.setIndex(getGame().getRandom().nextInt(4));

        mSpriteCanon = s.spriteCanon.yieldAnimated(Layers.TOWER);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setSequence(mSpriteCanon.sequenceForwardBackward());
        mSpriteCanon.setInterval(REBOUND_DURATION);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base1, 4);
        s.spriteBase.setMatrix(1f, 1f, null, null);

        s.spriteCanon = Sprite.fromResources(R.drawable.glue_gun, 6);
        s.spriteCanon.setMatrix(0.8f, 1.0f, new Vector2(0.4f, 0.4f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGame().add(mSpriteBase);
        getGame().add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        getGame().remove(mSpriteBase);
        getGame().remove(mSpriteCanon);
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (isReloaded() && getTarget() != null) {
            float dist = getDistanceTo(getTarget());
            float time = dist / GlueShot.MOVEMENT_SPEED;

            Vector2 target = getTarget().getPositionAfter(time);

            mAngle = getAngleTo(target);

            Vector2 position = Vector2.polar(SHOT_SPAWN_OFFSET, getAngleTo(target));
            position.add(getPosition());

            getGame().add(new GlueShot(this, position, target, 1f / getDamage(), mGlueDuration));

            setReloaded(false);
            mRebounding = true;
        }

        if (mRebounding && mSpriteCanon.tick()) {
            mRebounding = false;
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
