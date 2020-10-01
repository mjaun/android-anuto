package ch.logixisland.anuto.util.math;

public abstract class Function {

    public static Function constant(final float value) {
        return new Function() {
            @Override
            public float calculate(float input) {
                return value;
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
                return (float) Math.sin(input);
            }
        };
    }

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

    public Function invert() {
        return new Function() {
            @Override
            public float calculate(float input) {
                return 1f / Function.this.calculate(input);
            }
        };
    }

    public Function absolute() {
        return new Function() {
            @Override
            public float calculate(float input) {
                return Math.abs(Function.this.calculate(input));
            }
        };
    }

    public Function join(final Function f, final float at) {
        return new Function() {
            @Override
            public float calculate(float input) {
                if (input < at) {
                    return Function.this.calculate(input);
                }
                return f.calculate(input - at);
            }
        };
    }

    public Function repeat(final float at) {
        return new Function() {
            @Override
            public float calculate(float input) {
                return Function.this.calculate(input % at);
            }
        };
    }

    public abstract float calculate(float input);

    public SampledFunction sample() {
        return new SampledFunction(this);
    }

}
