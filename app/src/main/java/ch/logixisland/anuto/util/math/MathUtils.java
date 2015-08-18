package ch.logixisland.anuto.util.math;

public final class MathUtils {
    private MathUtils() {
    }

    public static float square(float x) {
        return x * x;
    }

    public static float sgn(float x) {
        return (x < 0f) ? -1f : 1f;
    }

    public static boolean equals(float x, float y, float d) {
        return Math.abs(x - y) <= d;
    }
}
