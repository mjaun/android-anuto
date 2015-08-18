package ch.logixisland.anuto.game.objects;

import ch.logixisland.anuto.game.TickTimer;
import ch.logixisland.anuto.game.TypeIds;

public abstract class Effect extends GameObject {

    /*
    ------ Constants ------
     */

    public static final int TYPE_ID = TypeIds.EFFECT;

    /*
    ------ Members ------
     */

    private TickTimer mTimer = new TickTimer();
    private boolean mEffectBegun = false;

    protected float mDuration = 0f;

    /*
    ------ Methods ------
     */

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init() {
        super.init();

        mTimer.setInterval(mDuration);
    }

    @Override
    public void tick() {
        super.tick();

        if (!mEffectBegun) {
            effectBegin();
            mEffectBegun = true;
        }

        if (mTimer != null && mTimer.tick()) {
            effectEnd();
            this.remove();
        }
    }

    protected abstract void effectBegin();

    protected abstract void effectEnd();
}
