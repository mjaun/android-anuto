package ch.logixisland.anuto.util.math;

public abstract class SampledFunction extends Function {

    /*
    ------ Members ------
     */

    private int mPosition;
    private float mValue;

    /*
    ------ Abstracts ------
     */

    public abstract float calculate(float input);

    /*
    ------ Methods -----
     */

    public int getPosition() {
        return mPosition;
    }

    public float getValue() {
        return mValue;
    }

    public SampledFunction setPosition(int position) {
        mPosition = position;
        mValue = calculate(mPosition);
        return this;
    }

    public SampledFunction step() {
        mPosition++;
        mValue = calculate(mPosition);
        return this;
    }

    public SampledFunction reset() {
        return setPosition(0);
    }
}
