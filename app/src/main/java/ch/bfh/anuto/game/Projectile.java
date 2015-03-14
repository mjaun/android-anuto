package ch.bfh.anuto.game;

import android.graphics.PointF;

public abstract class Projectile extends GameObject implements GameObject.Listener {
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
        target.addListener(this);
    }

    /*
    ------ Methods ------
     */

    protected float getDistanceToTarget() {
        return getDistanceTo(mTarget.getPosition());
    }

    protected PointF getDirectionToTarget() {
        return getDirectionTo(mTarget.getPosition());
    }

    @Override
    protected void onRemove() {
        mTarget.removeListener(this);
    }

    @Override
    public void onObjectRemove(GameObject obj) {
        mGame.removeObject(this);
    }
}
