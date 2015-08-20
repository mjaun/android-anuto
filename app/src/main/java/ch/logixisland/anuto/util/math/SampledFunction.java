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

    /*
    ------ Methods ------
     */

    public SampledFunction join(final Function f, final float at) {
        return new SampledFunction() {
            @Override
            public float calculate(float input) {
                if (input < at) {
                    return SampledFunction.this.calculate(input);
                } else {
                    return f.calculate(input);
                }
            }
        };
    }
}
