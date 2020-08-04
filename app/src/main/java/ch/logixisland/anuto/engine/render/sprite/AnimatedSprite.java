package ch.logixisland.anuto.engine.render.sprite;

import ch.logixisland.anuto.engine.logic.loop.TickTimer;

public class AnimatedSprite extends SpriteInstance {

    private final TickTimer mTimer = new TickTimer();

    private int mSequenceIndex;
    private int[] mSequence;

    AnimatedSprite(int layer, SpriteTemplate template) {
        super(layer, template);
    }

    @Override
    int getIndex() {
        return mSequence[mSequenceIndex];
    }

    public int getSequenceIndex() {
        return mSequenceIndex;
    }

    public void setFrequency(float frequency) {
        setInterval(1f / frequency);
    }

    public void setInterval(float interval) {
        mTimer.setInterval(interval / mSequence.length);
    }

    public void setSequence(int[] sequence) {
        mSequence = sequence;
        reset();
    }

    public void setSequenceForward() {
        int bitmapCount = getTemplate().getBitmapCount();
        int[] seq = new int[bitmapCount];

        for (int i = 0; i < seq.length; i++) {
            seq[i] = i;
        }

        setSequence(seq);
    }

    public void setSequenceForwardBackward() {
        int bitmapCount = getTemplate().getBitmapCount();
        int[] seq = new int[bitmapCount * 2 - 2];

        for (int i = 0; i < seq.length; i++) {
            if (i < bitmapCount) {
                seq[i] = i;
            } else {
                seq[i] = bitmapCount * 2 - 2 - i;
            }
        }

        setSequence(seq);
    }

    public void setSequenceBackward() {
        int bitmapCount = getTemplate().getBitmapCount();
        int[] seq = new int[bitmapCount];

        for (int i = 0; i < seq.length; i++) {
            seq[i] = bitmapCount - 1 - i;
        }

        setSequence(seq);
    }

    public void reset() {
        mTimer.reset();
        mSequenceIndex = 0;
    }

    public boolean tick() {
        boolean ret = false;

        if (mTimer.tick()) {
            if (mSequenceIndex >= mSequence.length - 1) {
                mSequenceIndex = 0;
                ret = true;
            } else {
                mSequenceIndex++;
            }
        }

        return ret;
    }

}
