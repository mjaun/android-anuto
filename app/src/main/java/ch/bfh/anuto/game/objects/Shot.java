package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.util.math.Vector2;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 4;
    public static final int LAYER = TYPE_ID * 100;

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
        moveSpeed(mDirection, mSpeed);
    }
}
