package ch.bfh.anuto.util.math;

public class Intersections {

    private Intersections() {
    }

    public Vector2[] lineCircle(Vector2 p1, Vector2 p2, float r) {
        Vector2 d = Vector2.fromTo(p1, p2);
        float dr2 = d.len2();
        float D = p1.x * p2.y - p2.x * p1.y;

        float discriminant = MathUtils.square(r) * dr2 - MathUtils.square(D);

        if (discriminant < 0) {
            return new Vector2[0];
        }

        Vector2[] ret = new Vector2[2];

        ret[0] = new Vector2(
                D * d.y + MathUtils.sgn(d.y) * d.x * discriminant,
                -D * d.x + Math.abs(d.y) * discriminant
        );

        ret[1] = new Vector2(
                D * d.y - MathUtils.sgn(d.y) * d.x * discriminant,
                -D * d.x - Math.abs(d.y) * discriminant
        );

        return ret;
    }
}
