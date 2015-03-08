package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Projectile extends GameObject {
    /*
    ------ Members ------
     */

    protected Tower mOwner;
    protected Enemy mTarget;

    /*
    ------ Constructors ------
     */

    public Projectile(Tower owner, Enemy target) {
        mOwner = owner;
        mTarget = target;

        setPosition(owner.getPosition());
    }

    /*
    ------ Public Methods ------
     */

    protected float getDistanceToTarget() {
        return getDistanceTo(mTarget.getPosition());
    }

    protected PointF getDirectionToTarget() {
        return getDirectionTo(mTarget.getPosition());
    }
}
