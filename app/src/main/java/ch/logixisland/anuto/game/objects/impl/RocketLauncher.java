package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Sprite;

public class RocketLauncher extends AimingTower {

    private final static float ROCKET_LOAD_TIME = 1.0f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite sprite;
        public Sprite spriteRocket; // used for preview only
    }

    private float mExplosionRadius;
    private float mAngle = 90f;
    private Rocket mRocket;
    private TickTimer mRocketLoadTimer;

    private Sprite.FixedInstance mSprite;
    private Sprite.FixedInstance mSpriteRocket; // used for preview only

    public RocketLauncher() {
        mExplosionRadius = getProperty("explosionRadius");

        StaticData s = (StaticData)getStaticData();

        mRocketLoadTimer = TickTimer.createInterval(ROCKET_LOAD_TIME);

        mSprite = s.sprite.yieldStatic(Layers.TOWER_BASE);
        mSprite.setListener(this);
        mSprite.setIndex(getGame().getRandom(4));

        mSpriteRocket = s.spriteRocket.yieldStatic(Layers.TOWER);
        mSpriteRocket.setListener(this);
        mSpriteRocket.setIndex(getGame().getRandom(4));
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.sprite = Sprite.fromResources(R.drawable.rocket_launcher, 4);
        s.sprite.setMatrix(1.1f, 1.1f, null, -90f);

        s.spriteRocket = Sprite.fromResources(R.drawable.rocket, 4);
        s.spriteRocket.setMatrix(0.8f, 1f, null, -90f);

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

        if (mRocket != null) {
            mRocket.remove();
        }
    }

    @Override
    public void enhance() {
        super.enhance();
        mExplosionRadius += getProperty("enhanceExplosionRadius");
    }

    @Override
    public void onDraw(DrawObject sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (mRocket == null && mRocketLoadTimer.tick()) {
            mRocket = new Rocket(this, getPosition(), getDamage(), mExplosionRadius);
            mRocket.setAngle(mAngle);
            getGame().add(mRocket);
        }

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (mRocket != null) {
                mRocket.setAngle(mAngle);

                if (isReloaded()) {
                    mRocket.setTarget(getTarget());
                    mRocket.setEnabled(true);
                    mRocket = null;

                    setReloaded(false);
                }
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSprite.draw(canvas);
        mSpriteRocket.draw(canvas);
    }
}
