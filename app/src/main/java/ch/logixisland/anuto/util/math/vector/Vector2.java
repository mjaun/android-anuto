package ch.logixisland.anuto.util.math.vector;

import ch.logixisland.anuto.util.math.MathUtils;

public class Vector2 {

    public static Vector2 polar(float length, float angle) {
        return new Vector2(
                (float) Math.cos(MathUtils.toRadians(angle)) * length,
                (float) Math.sin(MathUtils.toRadians(angle)) * length);
    }

    public static Vector2 fromTo(Vector2 p1, Vector2 p2) {
        return new Vector2(p2.x - p1.x, p2.y - p1.y);
    }

    public float x;
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
        return MathUtils.toDegrees((float) Math.atan2(y, x));
    }

}
