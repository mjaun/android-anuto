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

    private GameObject mOrigin;

    private TickTimer mTimer;
    private boolean mEffectBegun = false;

    private float mDuration;

    /*
    ------ Constructors ------
     */

    protected Effect(GameObject origin, float duration) {
        mOrigin = origin;
        mDuration = duration;
    }

    /*
    ------ Methods ------
     */

    public GameObject getOrigin() {
        return mOrigin;
    }

    public float getDuration() {
        return mDuration;
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public void init() {
        super.init();

        if (mDuration > 0f) {
            mTimer = TickTimer.createInterval(mDuration);
        }
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
