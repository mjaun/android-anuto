package ch.logixisland.anuto.entity.effect;

import ch.logixisland.anuto.engine.logic.Entity;
import ch.logixisland.anuto.engine.logic.TickTimer;
import ch.logixisland.anuto.entity.Types;

public abstract class Effect extends Entity {

    enum State {
        NotStarted,
        Active,
        Ended
    }

    private Entity mOrigin;
    private TickTimer mTimer;
    private State mState;

    Effect(Entity origin) {
        super(origin.getGameEngine());
        mOrigin = origin;
        mState = State.NotStarted;
    }

    Effect(Entity origin, float duration) {
        this(origin);
        mTimer = TickTimer.createInterval(duration);
    }

    public Entity getOrigin() {
        return mOrigin;
    }

    public State getState() {
        return mState;
    }

    @Override
    public final int getEntityType() {
        return Types.EFFECT;
    }

    @Override
    public void tick() {
        super.tick();

        if (mState == State.NotStarted) {
            mState = State.Active;
            effectBegin();
        }

        if (mTimer != null && mTimer.tick()) {
            mState = State.Ended;
            effectEnd();
            remove();
        }
    }

    protected void effectBegin() {

    }

    protected void effectEnd() {

    }
}
