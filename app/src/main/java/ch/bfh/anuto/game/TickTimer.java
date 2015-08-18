package ch.bfh.anuto.game;

public class TickTimer {

    public static TickTimer createInterval(float interval) {
        TickTimer ret = new TickTimer();
        ret.setInterval(interval);
        return ret;
    }

    public static TickTimer createFrequency(float frequency) {
        TickTimer ret = new TickTimer();
        ret.setFrequency(frequency);
        return ret;
    }

    private float mReloadValue = 0f;
    private float mValue = 0f;

    public TickTimer() {
    }

    public void setInterval(float interval) {
        mValue = mReloadValue = GameEngine.TARGET_FRAME_RATE * interval;

        if (mReloadValue < 1f) {
            throw new IllegalArgumentException("Too fast TickTimer frequency!");
        }
    }

    public void setFrequency(float frequency) {
        mValue = mReloadValue = GameEngine.TARGET_FRAME_RATE / frequency;

        if (mReloadValue < 1f) {
            throw new IllegalArgumentException("Too fast TickTimer frequency!");
        }
    }

    public boolean tick() {
        mValue -= 1f;

        if (mValue <= 0f) {
            mValue += mReloadValue;
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        mValue = mReloadValue;
    }
}
