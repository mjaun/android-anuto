package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.entity.effect.Laser;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.SpriteInstance;
import ch.logixisland.anuto.game.render.SpriteTemplate;
import ch.logixisland.anuto.game.render.StaticSprite;
import ch.logixisland.anuto.util.Random;
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
        mSpriteBase.setIndex(Random.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setIndex(Random.next(4));
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
    public void onDraw(SpriteInstance sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Vector2 from = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(getPosition());
                getGame().add(new Laser(this, from, getTarget(), getDamage()));
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
