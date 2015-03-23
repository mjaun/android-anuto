package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.GameObject;

public abstract class AreaEffect extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = 5;
    public static final int LAYER = TYPE_ID * 100;

    /*
    ------ Members ------
     */

    protected int mDelay = 0;
    protected boolean mEffectDone = false;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void tick() {
        if (mDelay > 0) {
            mDelay--;
        }

        if (mDelay <= 0 && !mEffectDone) {
            applyEffect();
            mEffectDone = true;
        }
    }

    protected void applyEffect() {
    }
}
