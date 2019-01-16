package ch.logixisland.anuto.util.math;

public class SampledFunction {

    private final Function mFunction;

    private int mPosition;
    private float mValue;

    SampledFunction(Function function) {
        mFunction = function;
        setPosition(0);
    }

    public int getPosition() {
        return mPosition;
    }

    public float getValue() {
        return mValue;
    }

    public SampledFunction setPosition(int position) {
        mPosition = position;
        mValue = mFunction.calculate(mPosition);
        return this;
    }

    public SampledFunction step() {
        mPosition++;
        mValue = mFunction.calculate(mPosition);
        return this;
    }

    public SampledFunction reset() {
        return setPosition(0);
    }
}
