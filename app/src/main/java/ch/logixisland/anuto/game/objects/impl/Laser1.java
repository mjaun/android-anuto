package ch.logixisland.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.game.Layers;
import ch.logixisland.anuto.game.objects.AreaEffect;
import ch.logixisland.anuto.game.objects.DrawObject;
import ch.logixisland.anuto.game.objects.Enemy;

public class Laser1 extends AreaEffect {

    private final static float DAMAGE = 1f;

    private final static float LASER_DRAW_WIDTH = 0.1f;
    private final static int LASER_DRAW_ALPHA = 180;

    private class LaserDrawObject extends DrawObject {
        private Paint mPaint;

        public LaserDrawObject() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(LASER_DRAW_WIDTH);
            mPaint.setColor(Color.MAGENTA);
            mPaint.setAlpha(LASER_DRAW_ALPHA);
        }

        @Override
        public int getLayer() {
            return Layers.SHOT;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawLine(mPosition.x, mPosition.y,
                    mTarget.getPosition().x, mTarget.getPosition().y, mPaint);
        }
    }

    private final Enemy mOrigin;
    private final Enemy mTarget;

    private final LaserDrawObject mDrawObject;

    public Laser1(Enemy origin, Enemy target) {
        mOrigin = origin;
        mTarget = target;

        if (mOrigin != null) {
            setPosition(mOrigin.getPosition());
        }

        mDrawObject = new LaserDrawObject();
    }

    @Override
    public void init() {
        super.init();

        mGame.add(mDrawObject);
    }

    @Override
    public void clean() {
        super.clean();

        mGame.remove(mDrawObject);
    }

    @Override
    public void tick() {
        super.tick();

        if (mOrigin != null) {
            setPosition(mOrigin.getPosition());
        }

        mTarget.damage(DAMAGE);
    }

    @Override
    protected void effectBegin() {

    }

    @Override
    protected void effectEnd() {

    }
}
