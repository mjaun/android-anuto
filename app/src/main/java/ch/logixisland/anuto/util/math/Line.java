package ch.logixisland.anuto.util.math;

public class Line {

    private final Vector2 mPoint1;
    private final Vector2 mPoint2;

    public Line(Vector2 point1, Vector2 point2) {
        if (point1 == null || point2 == null) {
            throw new IllegalArgumentException();
        }

        mPoint1 = point1;
        mPoint2 = point2;
    }

    public Vector2 getPoint1() {
        return mPoint1;
    }

    public Vector2 getPoint2() {
        return mPoint2;
    }

    public float length() {
        return mPoint1.distanceTo(mPoint2);
    }

    public float angle() {
        return mPoint1.angleTo(mPoint2);
    }

    public Vector2 direction() {
        return mPoint1.directionTo(mPoint2);
    }
}
