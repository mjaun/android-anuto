package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.shot.Rocket;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.Random;

public class RocketLauncher extends AimingTower {

    private final static float ROCKET_LOAD_TIME = 1.0f;

    private class StaticData {
        SpriteTemplate mSpriteTemplate;
        SpriteTemplate mSpriteTemplateRocket; // used for preview only
    }

    private float mExplosionRadius;
    private float mAngle = 90f;
    private Rocket mRocket;
    private TickTimer mRocketLoadTimer;

    private StaticSprite mSprite;
    private StaticSprite mSpriteRocket; // used for preview only

    public RocketLauncher() {
        mExplosionRadius = getProperty("explosionRadius");

        StaticData s = (StaticData)getStaticData();

        mRocketLoadTimer = TickTimer.createInterval(ROCKET_LOAD_TIME);

        mSprite = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplate);
        mSprite.setListener(this);
        mSprite.setIndex(Random.next(4));

        mSpriteRocket = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateRocket);
        mSpriteRocket.setListener(this);
        mSpriteRocket.setIndex(Random.next(4));
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplate = getSpriteFactory().createTemplate(R.drawable.rocket_launcher, 4);
        s.mSpriteTemplate.setMatrix(1.1f, 1.1f, null, -90f);

        s.mSpriteTemplateRocket = getSpriteFactory().createTemplate(R.drawable.rocket, 4);
        s.mSpriteTemplateRocket.setMatrix(0.8f, 1f, null, -90f);

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
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
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
