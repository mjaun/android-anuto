package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameObject;
import ch.bfh.anuto.game.TypeIds;
import ch.bfh.anuto.util.math.Vector2;

public abstract class Shot extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.SHOT;

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
        super.tick();
        moveSpeed(mDirection, mSpeed);
    }
}
