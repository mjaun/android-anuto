package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.effect.Laser;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.sprite.SpriteInstance;
import ch.logixisland.anuto.game.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.game.render.sprite.StaticSprite;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class LaserTower1 extends AimingTower {

    private final static float LASER_SPAWN_OFFSET = 0.7f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private float mAngle = 90f;

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteCanon;

    public LaserTower1() {
        StaticData s = (StaticData)getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setIndex(RandomUtils.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setIndex(RandomUtils.next(4));
        mSpriteCanon.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.drawable.base5, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, -90f);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.drawable.laser_tower1, 4);
        s.mSpriteTemplateCanon.setMatrix(0.4f, 0.9f, new Vector2(0.2f, 0.2f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSpriteBase);
        getGameEngine().add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSpriteBase);
        getGameEngine().remove(mSpriteCanon);
    }

    @Override
    public void draw(SpriteInstance sprite, Canvas canvas) {
        super.draw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Vector2 from = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(getPosition());
                getGameEngine().add(new Laser(this, from, getTarget(), getDamage()));
                setReloaded(false);
            }
        }
    }

    @Override
    public void preview(Canvas canvas) {
        mSpriteBase.draw(canvas);
        mSpriteCanon.draw(canvas);
    }
}
