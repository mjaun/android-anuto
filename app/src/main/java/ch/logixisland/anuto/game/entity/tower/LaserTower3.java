package ch.logixisland.anuto.game.entity.tower;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.effect.LaserStraight;
import ch.logixisland.anuto.game.render.Layers;
import ch.logixisland.anuto.game.render.Drawable;
import ch.logixisland.anuto.game.render.Sprite;
import ch.logixisland.anuto.util.Random;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class LaserTower3 extends AimingTower {

    private final static float LASER_SPAWN_OFFSET = 0.8f;

    private class StaticData {
        public Sprite spriteBase;
        public Sprite spriteCanon;
    }

    private float mAngle = 90f;
    private float mLaserLength;

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.FixedInstance mSpriteCanon;

    public LaserTower3() {
        mLaserLength = getGame().getGameSize().len() + 1f;

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setIndex(Random.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = s.spriteCanon.yieldStatic(Layers.TOWER);
        mSpriteCanon.setIndex(Random.next(4));
        mSpriteCanon.setListener(this);
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base5, 4);
        s.spriteBase.setMatrix(1f, 1f, null, -90f);

        s.spriteCanon = Sprite.fromResources(R.drawable.laser_tower3, 4);
        s.spriteCanon.setMatrix(0.4f, 1.2f, new Vector2(0.2f, 0.2f), -90f);

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
    public void onDraw(Drawable sprite, Canvas canvas) {
        super.onDraw(sprite, canvas);

        canvas.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Vector2 laserFrom = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(getPosition());
                Vector2 laserTo = Vector2.polar(mLaserLength, mAngle).add(getPosition());
                getGame().add(new LaserStraight(this, laserFrom, laserTo, getDamage()));
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
