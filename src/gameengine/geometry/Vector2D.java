package gameengine.geometry;

/**
 * @author davidrusu
 */
public class Vector2D {
    /**
     * The components of this vector.
     */
    private double x, y;

    /**
     * Creates a Vector2D instance at the origin.
     */
    public Vector2D() {
        this(0, 0);
    }

    /**
     * Creates a Vector2D instance by copying the specified vector.
     *
     * @param vector The vector to copy
     */
    public Vector2D(Vector2D vector) {
        this(vector.x, vector.y);
    }

    /**
     * Creates a Vector2D instance with the specified components.
     *
     * @param x The X component
     * @param y The Y component
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }

    public void set(Vector2D v) {
        set(v.x, v.y);
    }

    /**
     * Sets the components of this vector.
     *
     * @param x The X component
     * @param y The Y component
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the X component of this vector.
     *
     * @param x The X component
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the Y component of this vector.
     *
     * @param y The Y component
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Clears this vector setting the components to the origin.
     */
    public void clear() {
        set(0, 0);
    }

    /**
     * @return The X component.
     */
    public double getX() {
        return x;
    }

    /**
     * @return The Y component.
     */
    public double getY() {
        return y;
    }

    /**
     * @return The length of this vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * @return The length of this vector squared (to avoid expensive square root operation).
     */
    public double lengthSquared() {
        return x * x + y * y;
    }

    /**
     * Adds the specified vector to this vector.
     *
     * @param vector The vector to be added
     */
    public void add(Vector2D vector) {
        add(vector.x, vector.y);
    }

    /**
     * Adds the provided values to this vector.
     *
     * @param x the amount to add to the x component
     * @param y the amount to add to the y component
     */
    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Subtracts the specified vector from this vector.
     *
     * @param vector The vector to subtract
     */
    public void subtract(Vector2D vector) {
        subtract(vector.x, vector.y);
    }

    /**
     * Subtracts the specified values from this vector.
     *
     * @param x he amount to subtract to the x component
     * @param y he amount to subtract to the y component
     */
    public void subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
    }

    /**
     * Scales this vector by the specified scaling factor.
     *
     * @param factor The scaling factor
     */
    public void scale(double factor) {
        x *= factor;
        y *= factor;
    }

    /**
     * Makes this a unit vector by dividing by its length.
     */
    public void unit() {
        scale(1 / length());
    }

    /**
     * Rotates this vector.
     *
     * @param sinAngle The sin of the angle to be rotated by
     * @param cosAngle The cos of the angle to be rotated by
     */
    public void rotate(double sinAngle, double cosAngle) {
        set(perpendicularProduct(sinAngle, cosAngle), dotProduct(sinAngle, cosAngle));
    }

    /**
     * Modifies this vector to become perpendicular.
     */
    public void perpendicular() {
        set(-y, x);
    }

    /**
     * Computes the dot product of this vector with the specified vector.
     *
     * @param vector The vector to perform the dot product with
     * @return The dot product
     */
    public double dotProduct(Vector2D vector) {
        return dotProduct(vector.x, vector.y);
    }

    /**
     * Computes the dot product of this vector with the specified vector components.
     *
     * @param x The X component of the vector to perform the dot product with
     * @param y The Y component of the vector to perform the doct product with
     * @return The dot product
     */
    public double dotProduct(double x, double y) {
        return this.x * x + this.y * y;
    }

    /**
     * Computes the dot product of the 2 vectors.
     *
     * @param x1 The X component of the first vector
     * @param y1 The Y component of the first vector
     * @param x2 The X component of the second vector
     * @param y2 The Y component of the second vector
     * @return The dot product
     */
    public static double dotProduct(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }

    /**
     * Computes the perpendicular product of this vector with the specified vector.  The
     * perpendicular product is the dot product against the perpendicular of the specified vector.
     *
     * @param vector The vector to perform the perpendicular product with
     * @return The perpendicular product
     */
    public double perpendicularProduct(Vector2D vector) {
        return perpendicularProduct(vector.x, vector.y);
    }

    /**
     * Computes the perpendicular product of this vector with the specified vector.  The
     * perpendicular product is the dot product against the perpendicular of the specified vector.
     *
     * @param x The X component of the vector to perform the perpendicular product with
     * @param y The Y component of the vector to perform the perpendicular product with
     * @return The perpendicular product
     */
    public double perpendicularProduct(double x, double y) {
        return this.x * y - this.y * x;
    }

    /**
     * Interpreting the current vector as a line from the origin, checks if the specified point is
     * to the left of this line.  If horizontal then returns true if the point is above.
     *
     * @param point The point to check
     * @return True if the point is to the left of the line
     */
    public boolean isLeft(Vector2D point) {
        return perpendicularProduct(point) > 0;
    }

    public void project(Vector2D onTo) {
        double dist = dotProduct(onTo) / onTo.lengthSquared();
        set(dist * onTo.x, dist * onTo.y);
    }

    public void project(double ontoX, double ontoY) {
        double dist = (x * ontoX + y * ontoY) / (ontoX * ontoX + ontoY * ontoY);
        x = dist * ontoX;
        y = dist * ontoY;
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

    public static double scalarProject(double x, double y, double ontoX, double ontoY, double
            ontoLength) {
        return (x * ontoX + y * ontoY) / ontoLength;
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

    public double distToLine(double x1, double y1, double x2, double y2) {
        return distToLine(x, y, x1, y1, x2, y2);
    }

    public static double distToLine(double x, double y, double x1, double y1, double x2, double
            y2) {
        return Math.abs(signedDistToLine(x, y, x1, y1, x2, y2));
    }

    public double signedDistanceToLine(double x1, double y1, double x2, double y2) {
        return signedDistToLine(x, y, x1, y1, x2, y2);
    }

    public static double signedDistToLine(double x, double y, double x1, double y1, double x2,
                                          double y2) {
        double dx = x1 - x2;
        double dy = y2 - y1;
        double dist = x * dy + y * dx + x2 * y1 - x1 * y2;
        return dist / Math.sqrt(dx * dx + dy * dy);
    }

    public static double distToLineSquared(double x, double y, double x1, double y1, double x2,
                                           double y2) {
        double dx = x1 - x2;
        double dy = y2 - y1;
        double dist = x * dy + y * dx + x2 * y1 - x1 * y2;
        return dist * dist / (dx * dx + dy * dy);
    }

    public double distToLineSquared(double x1, double y1, double x2, double y2) {
        return distToLineSquared(x, y, x1, y1, x2, y2);
    }

    public static boolean isPointsProjectionWithinLine(double px, double py, double r1x, double
            r1y, double r2x, double r2y) {
        double dx = r1x - r2x;
        double dy = r1y - r2y;
        double x = px - r2x;
        double y = py - r2y;
        return dotProduct(x, y, dx, dy) >= 0 == dotProduct(x - dx, y - dy, dx, dy) <= 0;
    }

    /**
     * Checks if the line segments a1 - b1 and a2 - b2 intersect.
     *
     * @return True if the lines intersect
     */
    public static boolean lineSegmentsIntersect(Vector2D a1, Vector2D b1, Vector2D a2, Vector2D
            b2) {
        //the line segments don't intersect if both points of one line segment are on the same side
        // of the other line segment (and make sure to test both lines)
        double deltaX2 = b2.x - a2.x, deltaY2 = b2.y - a2.y;
        double deltaX12 = a1.x - a2.x, deltaY12 = a1.y - a2.y;

        if ((deltaX2 * deltaY12 - deltaY2 * deltaX12) > 0 == (deltaX2 * (b1.y - a2.y) - deltaY2 *
                (b1.x - a2.x)) > 0) {
            return false;
        }

        double deltaX1 = b1.x - a1.x, deltaY1 = b1.y - a1.y;
        return (deltaY1 * deltaX12 - deltaX1 * deltaY12) > 0 != (deltaX1 * (b2.y - a1.y) -
                deltaY1 * (b2.x - a1.x)) > 0;
    }
}
