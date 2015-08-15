package ch.bfh.anuto.game.objects.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.bfh.anuto.game.Layers;
import ch.bfh.anuto.game.objects.DrawObject;
import ch.bfh.anuto.game.objects.Enemy;
import ch.bfh.anuto.game.objects.GameObject;
import ch.bfh.anuto.game.objects.HomingShot;
import ch.bfh.anuto.util.math.Vector2;

public class Laser1 extends HomingShot {

    private final static float DAMAGE = 10f;
    private final static float MOVEMENT_SPEED = 8.0f;

    private class LaserDrawObject extends DrawObject {
        private Paint mPaint;

        public LaserDrawObject() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.RED);
            mPaint.setAlpha(200);
        }

        @Override
        public int getLayer() {
            return Layers.SHOT;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(mPosition.x, mPosition.y, 0.1f, mPaint);
        }
    }

    private LaserDrawObject mDrawObject;

    public Laser1(Vector2 position, Enemy target) {
        setPosition(position);
        setTarget(target);

        mSpeed = MOVEMENT_SPEED;

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
        mDirection = getDirectionTo(mTarget);
        super.tick();
    }

    @Override
    protected void onTargetLost() {
        Enemy closest = (Enemy)mGame.getGameObjects(Enemy.TYPE_ID)
                .min(GameObject.distanceTo(mPosition));

        if (closest == null) {
            this.remove();
        } else {
            setTarget(closest);
        }
    }

    @Override
    protected void onTargetReached() {
        mTarget.damage(DAMAGE);
        this.remove();
    }
}
