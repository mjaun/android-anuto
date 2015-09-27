package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AimingTower;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Sprite;
import ch.logixisland.anuto.util.math.Vector2;

public class LaserTower2 extends AimingTower {

    private final static float LASER_SPAWN_OFFSET = 0.7f;

    private class StaticData extends GameEngine.StaticData {
        public Sprite spriteBase;
        public Sprite spriteCanon;
    }

    private float mAngle = 90f;
    private int mBounce;
    private float mBounceDistance;

    private Sprite.FixedInstance mSpriteBase;
    private Sprite.FixedInstance mSpriteCanon;

    public LaserTower2() {
        mBounce = (int)getProperty("bounce");
        mBounceDistance = getProperty("bounceDistance");

        StaticData s = (StaticData)getStaticData();

        mSpriteBase = s.spriteBase.yieldStatic(Layers.TOWER_BASE);
        mSpriteBase.setIndex(getGame().getRandom(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = s.spriteCanon.yieldStatic(Layers.TOWER);
        mSpriteCanon.setIndex(getGame().getRandom(4));
        mSpriteCanon.setListener(this);
    }

    @Override
    public GameEngine.StaticData initStatic() {
        StaticData s = new StaticData();

        s.spriteBase = Sprite.fromResources(R.drawable.base5, 4);
        s.spriteBase.setMatrix(1f, 1f, null, -90f);

        s.spriteCanon = Sprite.fromResources(R.drawable.laser_tower2, 4);
        s.spriteCanon.setMatrix(0.4f, 1.0f, new Vector2(0.2f, 0.2f), -90f);

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

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Vector2 origin = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(getPosition());
                getGame().add(new Laser(this, origin, getTarget(), getDamage(), mBounce, mBounceDistance));
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
