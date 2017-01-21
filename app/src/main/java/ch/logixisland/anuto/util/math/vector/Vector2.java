package ch.logixisland.anuto.util.math.vector;

import org.simpleframework.xml.Attribute;

public class Vector2 {

    private static final float TO_RADIANS = (float) Math.PI / 180f;
    private static final float TO_DEGREES = 180f / (float) Math.PI;

    public static Vector2 polar(float length, float angle) {
        return new Vector2((float) Math.cos(angle * TO_RADIANS) * length,
                (float) Math.sin(angle * TO_RADIANS) * length);
    }

    public static Vector2 fromTo(Vector2 p1, Vector2 p2) {
        return new Vector2(p2.x - p1.x, p2.y - p1.y);
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

    @Attribute(name = "x")
    public float x;

    @Attribute(name = "y")
    public float y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        set(x, y);
    }

    public Vector2(Vector2 v) {
        set(v);
    }

    public Vector2 copy() {
        return new Vector2(this);
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public Vector2 mul(float s) {
        x *= s;
        y *= s;
        return this;
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float len2() {
        return x * x + y * y;
    }

    public Vector2 norm() {
        float len = len();

        if (len != 0) {
            x /= len;
            y /= len;
        }

        return this;
    }

    public Vector2 proj(Vector2 v) {
        float f = this.dot(v) / v.len2();

        this.x = v.x * f;
        this.y = v.y * f;
        return this;
    }

    public float angle() {
        return (float) Math.atan2(y, x) * TO_DEGREES;
    }

}
