package ch.logixisland.anuto.game.entity.effect;

import ch.logixisland.anuto.game.engine.TickTimer;
import ch.logixisland.anuto.game.entity.Entity;
import ch.logixisland.anuto.game.entity.Types;

public abstract class Effect extends Entity {

    private Entity mOrigin;
    private TickTimer mTimer;
    private boolean mEffectBegun;
    private float mDuration;

    protected Effect(Entity origin, float duration) {
        mOrigin = origin;
        mDuration = duration;
    }

    public Entity getOrigin() {
        return mOrigin;
    }

    @Override
    public final int getType() {
        return Types.EFFECT;
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
