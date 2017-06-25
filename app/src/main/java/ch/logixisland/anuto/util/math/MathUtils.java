package ch.logixisland.anuto.util.math;

public final class MathUtils {
    private MathUtils() {
    }

    public static float square(float x) {
        return x * x;
    }

    public static float sign(float x) {
        return (x < 0f) ? -1f : 1f;
    }

    public static boolean equals(float x, float y, float d) {
        return Math.abs(x - y) <= d;
    }

    public static float toRadians(float degrees) {
        return degrees / 180f * (float) Math.PI;
    }

    public static float toDegrees(float radians) {
        return radians / (float) Math.PI * 180f;
    }

    public static float normalizeAngle(float angle) {
        float ret = angle % 360f;

        if (ret > 180f) {
            ret -= 360f;
        } else if (ret < -180f) {
            ret += 360f;
        }

        return ret;
    }
}
