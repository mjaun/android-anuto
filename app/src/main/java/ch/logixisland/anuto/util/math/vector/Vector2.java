package ch.logixisland.anuto.util.math.vector;

import ch.logixisland.anuto.util.math.MathUtils;

public class Vector2 {

    public static Vector2 polar(float length, float angle) {
        return new Vector2(
                (float) Math.cos(MathUtils.toRadians(angle)) * length,
                (float) Math.sin(MathUtils.toRadians(angle)) * length
        );
    }

    private float x;
    private float y;

    public Vector2() {
        this.x = 0f;
        this.y = 0f;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public Vector2 add(Vector2 v) {
        return new Vector2(this.x + v.x, this.y + v.y);
    }

    public Vector2 sub(Vector2 v) {
        return new Vector2(this.x - v.x, this.y - v.y);
    }

    public Vector2 to(Vector2 v) {
        return new Vector2(v.x - this.x, v.y - this.y);
    }

    public Vector2 mul(float s) {
        return new Vector2(this.x * s, this.y * s);
    }

    public Vector2 div(float s) {
        return new Vector2(this.x / s, this.y / s);
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
        return this.div(this.len());
    }

    public Vector2 proj(Vector2 v) {
        float f = this.dot(v) / v.len2();
        return this.mul(f);
    }

    public float angle() {
        return MathUtils.toDegrees((float) Math.atan2(y, x));
    }

}
