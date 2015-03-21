package ch.bfh.anuto.game.objects;

import android.graphics.PointF;

import ch.bfh.anuto.game.GameObject;

public abstract class Tower extends GameObject implements GameObject.Listener {

    /*
    ------ Constants ------
     */

    public static final int TYPEID = 3;

    /*
    ------ Members ------
     */

    protected float mRange = 1000f;
    protected int mReloadTime = 20;

    protected int mReloadCounter = 0;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPEID;
    }

    @Override
    public void tick() {
        if (mReloadCounter > 0) {
            mReloadCounter--;
        }
    }


    protected boolean isReloaded() {
        return mReloadCounter <= 0;
    }

    protected void shoot(Shot shot) {
        mGame.addObject(shot);
        mReloadCounter = mReloadTime;
    }

}
