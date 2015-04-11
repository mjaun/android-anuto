package ch.bfh.anuto.game;

public class TickTimer {

    public static TickTimer createInterval(float interval) {
        TickTimer ret = new TickTimer();
        ret.setInterval(interval);
        return ret;
    }

    public static TickTimer createInterval(float interval, Sprite sprite) {
        TickTimer ret = new TickTimer();
        ret.setInterval(interval, sprite);
        return ret;
    }

    public static TickTimer createFrequency(float frequency) {
        TickTimer ret = new TickTimer();
        ret.setFrequency(frequency);
        return ret;
    }

    public static TickTimer createFrequency(float frequency, Sprite sprite) {
        TickTimer ret = new TickTimer();
        ret.setFrequency(frequency, sprite);
        return ret;
    }

    protected float mReloadValue = 0f;
    protected float mValue = 0f;

    public TickTimer() {
    }

    public void setInterval(float interval) {
        mValue = mReloadValue = GameEngine.TARGET_FPS * interval;
    }

    public void setFrequency(float frequency) {
        mValue = mReloadValue = GameEngine.TARGET_FPS / frequency;
    }

    public void setInterval(float interval, Sprite sprite) {
        setInterval(interval / (sprite.getCount() * 2 - 1));
    }

    public void setFrequency(float frequency, Sprite sprite) {
        setFrequency(frequency * (sprite.getCount() * 2 - 1));
    }

    public boolean tick() {
        if (mValue <= 0f) {
            mValue += mReloadValue;
            return true;
        } else {
            mValue -= 1f;
            return false;
        }
    }
}
