package ch.logixisland.anuto.util.math.vector;

public class Line {

    private Vector2 mPoint1;
    private Vector2 mPoint2;

    public Line() {

    }

    public Line(Vector2 point1, Vector2 point2) {
        mPoint1 = point1;
        mPoint2 = point2;
    }

    public Vector2 getPoint1() {
        return mPoint1;
    }

    public Vector2 getPoint2() {
        return mPoint2;
    }

    public void setPoint1(Vector2 point1) {
        mPoint1 = point1;
    }

    public void setPoint2(Vector2 point2) {
        mPoint2 = point2;
    }

    public Vector2 lineVector() {
        return Vector2.fromTo(mPoint1, mPoint2);
    }

    public float length() {
        return lineVector().len();
    }

    public float angle() {
        return lineVector().angle();
    }

}
