package ch.logixisland.anuto.game.objects;

import ch.logixisland.anuto.game.GameEngine;
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

    private GameObject mOrigin;

    private float mSpeed;
    private Vector2 mDirection;

    /*
    ------ Constructors ------
     */

    protected Shot(GameObject origin) {
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


    public GameObject getOrigin() {
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
