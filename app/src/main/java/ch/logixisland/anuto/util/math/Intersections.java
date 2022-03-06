package ch.logixisland.anuto.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Intersections {
    private Intersections() {
    }

    public static Collection<Line> getPathSectionsInRange(List<Vector2> wayPoints, Vector2 position, float range) {
        float r2 = MathUtils.square(range);
        Collection<Line> sections = new ArrayList<>();

        for (int i = 1; i < wayPoints.size(); i++) {
            Vector2 p1 = Vector2.to(position, wayPoints.get(i - 1));
            Vector2 p2 = Vector2.to(position, wayPoints.get(i));

            boolean p1in = p1.len2() <= r2;
            boolean p2in = p2.len2() <= r2;

            Vector2[] intersections = lineCircle(p1, p2, range);

            Vector2 sectionP1;
            Vector2 sectionP2;

            if (p1in && p2in) {
                sectionP1 = Vector2.add(p1, position);
                sectionP2 = Vector2.add(p2, position);
            } else if (!p1in && !p2in) {
                if (intersections == null) {
                    continue;
                }

                float a1 = intersections[0].angleTo(p1);
                float a2 = intersections[0].angleTo(p2);

                if (MathUtils.equals(a1, a2, 10f)) {
                    continue;
                }

                sectionP1 = Vector2.add(intersections[0], position);
                sectionP2 = Vector2.add(intersections[1], position);
            } else {
                float angle = p1.angleTo(p2);

                if (p1in) {
                    if (MathUtils.equals(angle, p1.angleTo(intersections[0]), 10f)) {
                        sectionP2 = Vector2.add(intersections[0], position);
                    } else {
                        sectionP2 = Vector2.add(intersections[1], position);
                    }

                    sectionP1 = Vector2.add(p1, position);
                } else {
                    if (MathUtils.equals(angle, intersections[0].angleTo(p2), 10f)) {
                        sectionP1 = Vector2.add(intersections[0], position);
                    } else {
                        sectionP1 = Vector2.add(intersections[1], position);
                    }

                    sectionP2 = Vector2.add(p2, position);
                }
            }

            sections.add(new Line(sectionP1, sectionP2));
        }

        return sections;
    }

    private static Vector2[] lineCircle(Vector2 p1, Vector2 p2, float r) {
        Vector2 d = Vector2.to(p1, p2);
        float dr2 = d.len2();
        float D = p1.x() * p2.y() - p2.x() * p1.y();

        float discriminant = MathUtils.square(r) * dr2 - MathUtils.square(D);

        if (discriminant < 0) {
            return null;
        }

        Vector2[] ret = new Vector2[2];

        discriminant = (float) Math.sqrt(discriminant);

        float y1 = (-D * d.x() + Math.abs(d.y()) * discriminant) / dr2;
        ret[0] = new Vector2((D * d.y() + MathUtils.sign(d.y()) * d.x() * discriminant) / dr2, y1);

        float y = (-D * d.x() - Math.abs(d.y()) * discriminant) / dr2;
        ret[1] = new Vector2((D * d.y() - MathUtils.sign(d.y()) * d.x() * discriminant) / dr2, y);

        return ret;
    }
}
