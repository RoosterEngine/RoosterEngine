package gameengine.geometry;

/**
 * @author davidrusu
 */
public class Vector2D {
    private double x, y;

    public Vector2D() {
        x = 0;
        y = 0;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v) {
        x = v.x;
        y = v.y;
    }

    public Vector2D setX(double x) {
        this.x = x;
        return this;
    }

    public Vector2D setY(double y) {
        this.y = y;
        return this;
    }

    public Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D set(Vector2D v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Adds the provided {@link Vector2D} to this {@link Vector2D}
     *
     * @param v the {@link Vector2D} you want to add to this {@link Vector2D}
     * @return itself
     */
    public Vector2D add(Vector2D v) {
        x += v.x;
        y += v.y;
        return this;
    }

    /**
     * Adds the provided values to this {@link Vector2D}
     *
     * @param x the amount to add to the x component
     * @param y the amount to add to the y component
     * @return itself
     */
    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Subtracts the provided {@link Vector2D} from this vector
     *
     * @param v the {@link Vector2D} to subtract
     * @return itself
     */
    public Vector2D subtract(Vector2D v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    /**
     * Subtracts the provided values from this {@link Vector2D}
     *
     * @param x he amount to subtract to the x component
     * @param y he amount to subtract to the y component
     * @return itself
     */
    public Vector2D subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    /**
     * Scales this {@link Vector2D} by the provided amount.
     *
     * @param a the amount to scale this {@link Vector2D} by.
     * @return itself
     */
    public Vector2D scale(double a) {
        x *= a;
        y *= a;
        return this;
    }

    /**
     * Divides this {@link Vector2D} by the provided amount
     *
     * @param a the amount to divide this {@link Vector2D} by.
     * @return itself
     */
    public Vector2D divide(double a) {
        x /= a;
        y /= a;
        return this;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public double dot(Vector2D v) {
        return x * v.x + y * v.y;
    }

    public static double dot(double x, double y, Vector2D v) {
        return x * v.x + y * v.y;
    }

    public static double dot(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }

    public Vector2D project(Vector2D onTo) {
        double dist = (x * onTo.x + y * onTo.y) / (onTo.x * onTo.x + onTo.y * onTo.y);
        x = dist * onTo.x;
        y = dist * onTo.y;
        return this;
    }

    public Vector2D project(double ontoX, double ontoY) {
        double dist = (x * ontoX + y * ontoY) / (ontoX * ontoX + ontoY * ontoY);
        x = dist * ontoX;
        y = dist * ontoY;
        return this;
    }

    public double unitScalarProject(Vector2D onto) {
        return x * onto.x + y * onto.y;
    }

    public static double unitScalarProject(double x, double y, Vector2D onto) {
        return x * onto.x + y * onto.y;
    }

    public static double unitScalarProject(double x, double y, double ontoX, double ontoY) {
        return x * ontoX + y * ontoY;
    }

    public double scalarProject(Vector2D onTo, double onToLength) {
        return (x * onTo.x + y * onTo.y) / onToLength;
    }

    public static double scalarProject(double x, double y, Vector2D onTo, double onToLength) {
        return (x * onTo.x + y * onTo.y) / onToLength;
    }

    public double scalarProject(double ontoX, double ontoY, double ontoLength) {
        return (x * ontoX + y * ontoY) / ontoLength;
    }

    public static double scalarProject(double x, double y, double ontoX, double ontoY, double ontoLength) {
        return (x * ontoX + y * ontoY) / ontoLength;
    }

    public static double distToLine(double x, double y, double x1, double y1, double x2, double y2) {
        return Math.abs(signedDistToLine(x, y, x1, y1, x2, y2));
    }

    public static double distToLineSquared(double x, double y, double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y2 - y1;
        double dist = x * dy + y * dx + x2 * y1 - x1 * y2;
        return dist * dist / (dx * dx + dy * dy);
    }

    public double distToLineSquared(double x1, double y1, double x2, double y2) {
        return distToLineSquared(x, y, x1, y1, x2, y2);
    }

    public double distToLine(double x1, double y1, double x2, double y2) {
        return distToLine(x, y, x1, y1, x2, y2);
    }

    /**
     * The distance from this point to the specified line
     * p1 cannot be the same as p2
     *
     * @param p1 the first point of the line
     * @param p2 the second point of the line
     * @return the distance from this point to the specified line
     */
    public double distToLine(Vector2D p1, Vector2D p2) {
        return Math.abs(signedDistToLine(x, y, p1.x, p1.y, p2.x, p2.y));
    }

    public static double signedDistToLine(double x, double y, double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y2 - y1;
        double dist = x * dy + y * dx + x2 * y1 - x1 * y2;
        return dist / Math.sqrt(dx * dx + dy * dy);
    }

    public double signedDistanceToLine(double x1, double y1, double x2, double y2) {
        return signedDistToLine(x, y, x1, y1, x2, y2);
    }

    public Vector2D unit() {
        double length = length();
        this.x /= length;
        this.y /= length;
        return this;
    }

    public Vector2D unit(double length) {
        this.x /= length;
        this.y /= length;
        return this;
    }

    public static boolean isPointsProjectionWithinLine(double px, double py, double r1x, double r1y, double r2x, double r2y) {
        double dx = r1x - r2x;
        double dy = r1y - r2y;
        double x = px - r2x;
        double y = py - r2y;
        return dot(x, y, dx, dy) >= 0 == dot(x - dx, y - dy, dx, dy) <= 0;
    }

    public void clear() {
        x = 0;
        y = 0;
    }

    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }
}