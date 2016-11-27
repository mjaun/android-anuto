package ch.logixisland.anuto.game.entity.shot;

import ch.logixisland.anuto.game.GameEngine;
import ch.logixisland.anuto.game.entity.Types;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.util.math.vector.Vector2;

public abstract class Shot extends Entity {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = Types.SHOT;

    /*
    ------ Members ------
     */

    private Entity mOrigin;

    private float mSpeed;
    private Vector2 mDirection;

    /*
    ------ Constructors ------
     */

    protected Shot(Entity origin) {
        mOrigin = origin;
    }

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

        if (isEnabled()) {
            move(mDirection, mSpeed / GameEngine.TARGET_FRAME_RATE);
        }
    }


    public Entity getOrigin() {
        return mOrigin;
    }

    public float getSpeed() {
        return mSpeed;
    }

    protected void setSpeed(float speed) {
        this.mSpeed = speed;
    }

    public Vector2 getDirection() {
        return mDirection;
    }

    protected void setDirection(Vector2 direction) {
        this.mDirection = direction;
    }
}
