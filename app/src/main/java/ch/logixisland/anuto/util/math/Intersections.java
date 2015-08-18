package ch.logixisland.anuto.util.math;

public final class Intersections {
    private Intersections() {
    }

    public static Vector2[] lineCircle(Vector2 p1, Vector2 p2, float r) {
        Vector2 d = Vector2.fromTo(p1, p2);
        float dr2 = d.len2();
        float D = p1.x * p2.y - p2.x * p1.y;

        float discriminant = MathUtils.square(r) * dr2 - MathUtils.square(D);

        if (discriminant < 0) {
            return null;
        }

        Vector2[] ret = new Vector2[2];

        discriminant = (float)Math.sqrt(discriminant);

        ret[0] = new Vector2(
                (D * d.y + MathUtils.sgn(d.y) * d.x * discriminant) / dr2,
                (-D * d.x + Math.abs(d.y) * discriminant) / dr2
        );

        ret[1] = new Vector2(
                (D * d.y - MathUtils.sgn(d.y) * d.x * discriminant) / dr2,
                (-D * d.x - Math.abs(d.y) * discriminant) / dr2
        );

        return ret;
    }
}
