package ch.bfh.anuto.game.objects;

import java.util.Iterator;
import java.util.List;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.util.Vector2;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 4;

    /*
    ------ Members ------
     */

    protected float mSpeed;
    protected Vector2 mDirection;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        move(mDirection, mSpeed);
    }
}
