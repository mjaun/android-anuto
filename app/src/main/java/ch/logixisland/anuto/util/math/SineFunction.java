package ch.logixisland.anuto.util.math;

public class SineFunction implements Function {

    float mAngle;
    float mValue;

    float mStartAngle = 0f;
    float mStopAngle = (float)Math.PI;
    float mStep;

    float mAmplitude = 1f;
    float mOffset = 0f;

    public void setSection(float stepCount) {
        mStep = (mStopAngle - mStartAngle) / stepCount;
        reset();
    }

    public void setProperties(float startAngle, float stopAngle, float amplitude, float offset) {
        mStartAngle = startAngle;
        mStopAngle = stopAngle;
        mAmplitude = amplitude;
        mOffset = offset;
        reset();
    }

    @Override
    public void reset() {
        mAngle = mStartAngle;
    }

    @Override
    public boolean step() {
        mAngle += mStep;
        mValue = mOffset + mAmplitude * (float)Math.sin(mAngle);

        return mAngle >= mStopAngle;
    }

    @Override
    public float getValue() {
        return mValue;
    }
}
