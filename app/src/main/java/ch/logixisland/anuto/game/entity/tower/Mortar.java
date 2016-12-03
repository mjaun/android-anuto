package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.shot.MortarShot;
import ch.logixisland.anuto.game.render.AnimatedSprite;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.Random;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class Mortar extends AimingTower {

    private final static float SHOT_SPAWN_OFFSET = 0.6f;
    private final static float REBOUND_DURATION = 0.5f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private float mInaccuracy;
    private float mExplosionRadius;
    private float mAngle = 90f;
    private boolean mRebounding = false;

    private StaticSprite mSpriteBase;
    private AnimatedSprite mSpriteCanon;

    public Mortar() {
        mInaccuracy = getProperty("inaccuracy");
        mExplosionRadius = getProperty("explosionRadius");

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setIndex(Random.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createAnimated(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setListener(this);
        mSpriteCanon.setSequenceForwardBackward();
        mSpriteCanon.setInterval(REBOUND_DURATION);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.drawable.base2, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, null);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.drawable.mortar, 8);
        s.mSpriteTemplateCanon.setMatrix(0.8f, null, new Vector2(0.4f, 0.2f), -90f);

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
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
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
            targetPos.add(Vector2.polar(Random.next(mInaccuracy), Random.next(360f)));
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
