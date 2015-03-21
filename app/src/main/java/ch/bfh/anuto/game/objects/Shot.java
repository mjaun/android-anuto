package ch.bfh.anuto.game.objects;

import android.graphics.PointF;

import ch.bfh.anuto.game.GameObject;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPEID = 4;

    /*
    ------ Members ------
     */

    protected float mSpeed = 1f;
    protected final PointF mDirection = new PointF(1f, 0f);

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPEID;
    }

    @Override
    public void tick() {
        move(mDirection, mSpeed);
    }
}
