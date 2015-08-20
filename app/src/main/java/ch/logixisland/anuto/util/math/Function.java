package ch.logixisland.anuto.util.math;

public abstract class Function {

    /*
    ------ Static ------
     */

    public static Function zero() {
        return new Function() {
            @Override
            public float calculate(float input) {
                return 0f;
            }
        };
    }

    public static Function linear() {
        return new Function() {
            @Override
            public float calculate(float input) {
                return input;
            }
        };
    }

    public static Function quadratic() {
        return new Function() {
            @Override
            public float calculate(float input) {
                return input * input;
            }
        };
    }

    public static Function sine() {
        return new Function() {
            @Override
            public float calculate(float input) {
                return (float)Math.sin(input);
            }
        };
    }

    /*
    ------ Abstract ------
     */

    public abstract float calculate(float input);

    /*
    ------ Methods ------
     */

    public Function multiply(final float x) {
        return new Function() {
            @Override
            public float calculate(float input) {
                return Function.this.calculate(input) * x;
            }
        };
    }

    public Function stretch(final float x) {
        return new Function() {
            @Override
            public float calculate(float input) {
                return Function.this.calculate(input / x);
            }
        };
    }

    public Function offset(final float d) {
        return new Function() {
            @Override
            public float calculate(float input) {
                return Function.this.calculate(input) + d;
            }
        };
    }

    public Function shift(final float d) {
        return new Function() {
            @Override
            public float calculate(float input) {
                return Function.this.calculate(input + d);
            }
        };
    }

    public SampledFunction sample() {
        return new SampledFunction() {
            @Override
            public float calculate(float input) {
                return Function.this.calculate(input);
            }
        };
    }
}
