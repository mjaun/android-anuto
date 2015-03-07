package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Projectile extends GameObject {
    protected Tower mOwner;
    protected Enemy mTarget;

    public Projectile(Tower owner, Enemy target) {
        mOwner = owner;
        mTarget = target;

        setPosition(owner.getPosition());
    }

    // TODO: why the heck couldn't I find 2D vector classes?

    protected float getDistanceToTarget() {
        PointF target = mTarget.getPosition();
        return (float)Math.sqrt(Math.pow(target.x - mPosition.x, 2) + Math.pow(target.y - mPosition.y, 2));
    }

    protected PointF getDirectionToTarget() {
        PointF target = mTarget.getPosition();
        float dist = getDistanceToTarget();
        float x = (target.x - mPosition.x) / dist;
        float y = (target.y - mPosition.y) / dist;
        return new PointF(x, y);
    }
}
