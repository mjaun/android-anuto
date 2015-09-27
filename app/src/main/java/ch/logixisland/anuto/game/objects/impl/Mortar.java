package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class Mortar extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.6f;
    private final static float REBOUND_DURATION = 0.5f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteCanon;
    }

    private float mInaccuracy;
    private float mExplosionRadius;
    private float mAngle = 90f;
    private boolean mRebounding = false;

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.AnimatedInstance mSpriteCanon;

    public Mortar() {
        mInaccuracy = getProperty("inaccuracy");
        mExplosionRadius = getProperty("explosionRadius");

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setIndex(getGame().getRandom(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = s.spriteCanon.yieldAnimated(Layers.TOWER);
        mSpriteCanon.setIndex(getGame().getRandom(4));
        mSpriteCanon.setListener(this);
        mSpriteCanon.setSequence(mSpriteCanon.sequenceForwardBackward());
        mSpriteCanon.setInterval(REBOUND_DURATION);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base2, 4);
        s.spriteBase.setMatrix(1f, 1f, null, null);

        s.spriteCanon = Sprite.fromResources(R.drawable.mortar, 8);
        s.spriteCanon.setMatrix(0.8f, null, new Vector2(0.4f, 0.2f), -90f);

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
    public void enhance() {
        super.enhance();
        mExplosionRadius += getProperty("enhanceExplosionRadius");
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        if (sprite == mSpriteCanon) {
            canvas.rotate(mAngle);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null && isReloaded()) {
            Vector2 targetPos = getTarget().getPositionAfter(MortarShot.TIME_TO_TARGET);
            targetPos.add(Vector2.polar(getGame().getRandom(mInaccuracy), getGame().getRandom(360f)));
            Vector2 shotPos = getPosition().copy().add(Vector2.polar(SHOT_SPAWN_OFFSET, mAngle));
            mAngle = getAngleTo(targetPos);

            getGame().add(new MortarShot(this, shotPos, targetPos, getDamage(), mExplosionRadius));

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
