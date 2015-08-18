package ch.logixisland.anuto.game.objects;

import ch.logixisland.anuto.game.TypeIds;
import ch.logixisland.anuto.util.math.Vector2;

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

        if (mEnabled) {
            moveSpeed(mDirection, mSpeed);
        }
    }
}
