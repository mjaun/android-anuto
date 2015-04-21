package ch.bfh.anuto.game.objects;

import ch.bfh.anuto.game.TypeIds;

public abstract class AreaEffect extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.AREA_EFFECT;

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
    public void onTick() {
        super.onTick();

        if (mDelay > 0) {
            mDelay--;
        }

        if (mDelay <= 0 && !mEffectDone) {
            applyEffect();
            mEffectDone = true;
        }
    }

    protected abstract void applyEffect();
}
