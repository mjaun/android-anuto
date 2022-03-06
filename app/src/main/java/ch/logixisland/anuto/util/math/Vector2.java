package ch.logixisland.anuto.util.math;

public class Vector2 {

    public static Vector2 polar(float length, float angle) {
        return new Vector2(
                (float) Math.cos(MathUtils.toRadians(angle)) * length,
                (float) Math.sin(MathUtils.toRadians(angle)) * length
        );
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    public static Vector2 sub(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public static Vector2 to(Vector2 a, Vector2 b) {
        return new Vector2(b.x - a.x, b.y - a.y);
    }

    public static Vector2 mul(Vector2 v, float s) {
        return new Vector2(v.x * s, v.y * s);
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

    // Like add() but overwrites the source object instead of allocating
    public Vector2 add(Vector2 v) {
        this.x = this.x + v.x;
        this.y = this.y + v.y;
        return this;
    }

    // Like mul() but overwrites the source object instead of allocating
    public Vector2 mul(float s) {
        this.x = this.x * s;
        this.y = this.y * s;
        return this;
    }

    public Vector2 div(float s) {
        return new Vector2(this.x / s, this.y / s);
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    private static float len(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float len() {
        return len(x, y);
    }

    public float len2() {
        return x * x + y * y;
    }

    public Vector2 norm() {
        return this.div(this.len());
    }

    public Vector2 proj(Vector2 v) {
        float f = this.dot(v) / v.len2();
        return Vector2.mul(v, f);
    }

    private static float angle(float x, float y) {
        return MathUtils.toDegrees((float) Math.atan2(y, x));
    }

    public float angle() {
        return angle(this.x, this.y);
    }

    // equivalent to v.to(x).len()
    public float distanceTo(Vector2 v) {
        return len(v.x - this.x, v.y - this.y);
    }

    // equivalent to v.to(x).angle()
    public float angleTo(Vector2 v) {
        return angle(v.x - this.x, v.y - this.y);
    }

    public Vector2 directionTo(Vector2 v) {
        Vector2 to = Vector2.to(this, v);
        float len = to.len();
        to.x /= len;
        to.y /= len;
        return to;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
