package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.geometry.Vector2D;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;

import java.util.Random;

public class Polygon extends Shape {
    private static Random rand = new Random(0);
    private final Vector2D[] normals, points; // points are relative to the center
    private final double[] normalMins, normalMaxs;
    private final double width, height, minX, maxX, minY, maxY;
    private final int numPoints;

    public Polygon(double[] xPoints, double[] yPoints) {
        super(getHalfLength(xPoints), getHalfLength(yPoints));
        numPoints = xPoints.length;
        points = new Vector2D[numPoints];
        normals = new Vector2D[numPoints];
        normalMins = new double[numPoints];
        normalMaxs = new double[numPoints];
        setupPoints(xPoints, yPoints);
        setupNormalsAndShadows();

        //setup max / min (moved to constructor to allow declaring immutable
        double tempMinX = Double.MAX_VALUE;
        double tempMaxX = -Double.MAX_VALUE;
        double tempMinY = Double.MAX_VALUE;
        double tempMaxY = -Double.MAX_VALUE;
        for (Vector2D point : points) {
            double x = point.getX();
            double y = point.getY();
            if (x < tempMinX) {
                tempMinX = x;
            }
            if (x > tempMaxX) {
                tempMaxX = x;
            }
            if (y < tempMinY) {
                tempMinY = y;
            }
            if (y > tempMaxY) {
                tempMaxY = y;
            }
        }
        maxX = tempMaxX;
        maxY = tempMaxY;
        minX = tempMinX;
        minY = tempMinY;

        width = maxX - minX;
        height = maxY - minY;
    }

    public static Polygon getCircle(double radius, int points) {
        double angle = 2 * Math.PI / points;
        double[] xPoints = new double[points];
        double[] yPoints = new double[points];
        double currentAngle = 0;
        for (int i = 0; i < points; i++) {
            currentAngle += angle;
            double x = Math.sin(currentAngle) * radius;
            double y = Math.cos(currentAngle) * radius;
            xPoints[i] = x;
            yPoints[i] = y;
        }
        return new Polygon(xPoints, yPoints);
    }

    public static Polygon getRandConvexPolygon(double radiusMin, double radiusMax, int
            numPointsMin, int numPointsMax) {
        double radius = rand.nextDouble() * (radiusMax - radiusMin) + radiusMin;
        int pointsDiff = numPointsMax - numPointsMin;
        int numPoints = (int) (rand.nextDouble() * pointsDiff) + numPointsMin;
        double[] xPoints = new double[numPoints];
        double[] yPoints = new double[numPoints];
        double angle = 2 * Math.PI / numPoints;
        double initAngle = rand.nextDouble() * 2 * Math.PI;
        for (int p = 0; p < numPoints; p++) {
            double randomAngle = rand.nextDouble() * angle / 2 - angle;
            double angleFluctuated = angle * p + randomAngle + initAngle;
            double pX = Math.cos(angleFluctuated) * radius;
            double pY = Math.sin(angleFluctuated) * radius;
            xPoints[p] = pX;
            yPoints[p] = pY;
        }
        return new Polygon(xPoints, yPoints);
    }

    private static double getHalfLength(double[] points) {
        double halfLength = 0;
        for (int i = 0; i < points.length; i++) {
            halfLength = Math.max(Math.abs(points[i]), halfLength);
        }
        return halfLength;
    }

    @Override
    public void collideWithShape(Entity current, Entity other, double maxTime, Collision result) {
        other.getShape().collideWithPolygon(other, current, this, maxTime, result);
    }

    @Override
    public void collideWithCircle(Entity current, Entity other, Circle circleShape, double
            maxTime, Collision result) {
        Shape.collideCirclePoly(other, circleShape, current, this, maxTime, result);
    }

    @Override
    public void collideWithRectangle(Entity current, Entity other, Rectangle aabbShape, double
            maxTime, Collision result) {
        Shape.collideRectanglePoly(other, aabbShape, current, this, maxTime, result);
    }

    @Override
    public void collideWithPolygon(Entity current, Entity other, Polygon polygonShape, double
            maxTime, Collision result) {
        Shape.collidePolyPoly(current, this, other, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Entity current, Entity other) {
        return other.getShape().isOverlappingPolygon(other, current, this);
    }

    @Override
    public boolean isOverlappingPolygon(Entity current, Entity other, Polygon shape) {
        return Shape.isOverlappingPolyPoly(current, this, other, shape);
    }

    @Override
    public boolean isOverlappingCircle(Entity current, Entity other, Circle shape) {
        return Shape.isOverlappingPolyCircle(current, this, other, shape);
    }

    @Override
    public boolean isOverlappingRectangle(Entity current, Entity other, Rectangle shape) {
        return Shape.isOverlappingPolyRectangle(current, this, other, shape);
    }

    /**
     * the first normal in the array is for the line points[points.length - 1] and points[0],
     * the second normal is for line points[0] and points[1].
     *
     * @return an array of {@link Vector2D} point outwards
     */
    public Vector2D[] getNormals() {
        return normals;
    }

    public double[] getNormalMins() {
        return normalMins;
    }

    public double[] getNormalMaxs() {
        return normalMaxs;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * Points are relative to the center
     *
     * @return {@link Vector2D} array with points that are relative to the center
     */
    public Vector2D[] getPoints() {
        return points;
    }

    public int getNumPoints() {
        return numPoints;
    }

    @Override
    public double getArea() {
        double sum = 0;
        double lastX = points[numPoints - 1].getX(), lastY = points[numPoints - 1].getY();
        for (int i = 0; i < numPoints; i++) {
            double x = points[i].getX();
            double y = points[i].getY();
            sum += lastX * y - lastY * x;
            lastX = x;
            lastY = y;
        }
        return Math.abs(sum * 0.5);
    }

    @Override
    public void draw(Renderer renderer, double x, double y) {
        renderer.fillPolygon(points, x, y);
    }

    private void drawPoints(Renderer renderer, RColor color) {
        int radius = 2;
        renderer.setForegroundColor(color);
        for (int i = 0; i < numPoints; i++) {
            Vector2D point = points[i];
            renderer.fillCircle(point.getX(), point.getY(), radius);
        }
    }

    private void drawNormals(Renderer renderer, double x, double y, RColor color) {
        double scale = 100;
        renderer.setForegroundColor(color);
        for (Vector2D normal : normals) {
            renderer.drawLine(x, y, x + normal.getX() * scale, y + normal.getY() * scale);
        }
    }

    private void setupPoints(double[] xPoints, double[] yPoints) {
        for (int i = 0; i < numPoints; i++) {
            points[i] = new Vector2D(xPoints[i], yPoints[i]);
        }
    }

    private final void setupNormalsAndShadows() {
        Vector2D point = points[numPoints - 1];
        double lastX = point.getX();
        double lastY = point.getY();
        for (int i = 0; i < numPoints; i++) {
            point = points[i];
            double x = point.getX();
            double y = point.getY();
            Vector2D normal = new Vector2D(y - lastY, lastX - x);
            normal.unit();
            normals[i] = normal;
            lastX = x;
            lastY = y;

            //finding the min and max when each point is projected onto the normal
            //used for collision detection
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for (int q = 0; q < numPoints; q++) {
                double dist = points[q].unitScalarProject(normal);
                if (dist < min) {
                    min = dist;
                }
                if (dist > max) {
                    max = dist;
                }
            }
            normalMins[i] = min;
            normalMaxs[i] = max;
        }
    }
}
