package ch.logixisland.anuto.util.math;

import java.util.ArrayList;
import java.util.List;

public class Vector2 {

    public static Vector2 polar(float length, float angle) {
        return new Vector2(
                (float) Math.cos(MathUtils.toRadians(angle)) * length,
                (float) Math.sin(MathUtils.toRadians(angle)) * length
        );
    }

    private float x;
    private float y;

    public static String serializeList(List<Vector2> vectors) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < vectors.size(); i++) {
            if (i > 0) {
                builder.append(";");
            }
            builder.append(vectors.get(i).toString());
        }
        return builder.toString();
    }

    public static List<Vector2> deserializeList(String string) {
        String[] parts = string.split(";");
        List<Vector2> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Vector2.parseVector(part));
        }
        return result;
    }

    public static Vector2 parseVector(String string) {
        String[] parts = string.split(",");
        return new Vector2(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
    }

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
        return v.mul(f);
    }

    public float angle() {
        return MathUtils.toDegrees((float) Math.atan2(y, x));
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
