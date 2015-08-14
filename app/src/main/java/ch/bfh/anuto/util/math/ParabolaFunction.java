package ch.bfh.anuto.util.math;

public class ParabolaFunction implements Function {

    private float mA = -1f;
    private float mB = 0f;
    private float mC = 1f;

    private float mPosition = -1f;
    private float mValue;
    private float mStep;

    public void setProperties(float start, float stop, float peak) {
        mB = (stop - start) / 2;

        float v = (stop + start) / 2;
        float a1 = ((v - peak) + (float)Math.sqrt(MathUtils.square(peak - v) - MathUtils.square(mB))) / 2;
        float a2 = ((v - peak) - (float)Math.sqrt(MathUtils.square(peak - v) - MathUtils.square(mB))) / 2;

        mA = (Math.abs(a1) > Math.abs(a2)) ? a1 : a2;
        mC = v - mA;
    }

    public void setSection(float stepCount) {
        mStep = 2f / stepCount;
    }

    @Override
    public void reset() {
        mPosition = -1f;
    }

    @Override
    public boolean step() {
        mPosition += mStep;
        mValue = mA * MathUtils.square(mPosition) + mB * mPosition + mC;
        return mPosition >= 1f;
    }

    @Override
    public float getValue() {
        return mValue;
    }
}
