package ch.logixisland.anuto.engine.logic.loop;

import ch.logixisland.anuto.engine.logic.GameEngine;

public class TickTimer {

    public static TickTimer createInterval(float interval) {
        TickTimer ret = new TickTimer();
        ret.setInterval(interval);
        return ret;
    }

    private float mReloadValue = 0f;
    private float mValue = 0f;

    public void setInterval(float interval) {
        mValue = mReloadValue = GameEngine.TARGET_FRAME_RATE * interval;
    }

    public void reset() {
        mValue = mReloadValue;
    }

    public boolean tick() {
        mValue -= 1f;

        if (mValue <= 0f) {
            mValue += mReloadValue;
            return true;
        }
        return false;
    }

}
