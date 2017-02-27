package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.geometry.Vector2D;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;

import java.util.Random;

public class Polygon extends Shape {
    private static Random rand = new Random(0);
    private Vector2D[] normals, points; // points are relative to the center
    private double[] normalMins, normalMaxs;
    private double width, height, minX, maxX, minY, maxY;
    private int numPoints;

    public Polygon(double[] xPoints, double[] yPoints) {
        super(getHalfLength(xPoints), getHalfLength(yPoints));
        numPoints = xPoints.length;
        points = new Vector2D[numPoints];
        normals = new Vector2D[numPoints];
        normalMins = new double[numPoints];
        normalMaxs = new double[numPoints];
        setupPoints(xPoints, yPoints);
        setupMaxMin();
        setupNormalsAndShadows();
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
    public void collideWithShape(Shape shape, double maxTime, Collision result) {
        shape.collideWithPolygon(this, maxTime, result);
    }

    @Override
    public void collideWithCircle(Circle circleShape, double maxTime, Collision result) {
        Shape.collideCirclePoly(circleShape, this, maxTime, result);
    }

    @Override
    public void collideWithRectangle(Rectangle aabbShape, double maxTime, Collision result) {
        Shape.collideRectanglePoly(aabbShape, this, maxTime, result);
    }

    @Override
    public void collideWithPolygon(Polygon polygonShape, double maxTime, Collision result) {
        Shape.collidePolyPoly(this, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Shape shape) {
        return shape.isOverlappingPolygon(this);
    }

    @Override
    public boolean isOverlappingPolygon(Polygon shape) {
        return Shape.isOverlappingPolyPoly(this, shape);
    }

    @Override
    public boolean isOverlappingCircle(Circle shape) {
        return Shape.isOverlappingPolyCircle(this, shape);
    }

    @Override
    public boolean isOverlappingRectangle(Rectangle shape) {
        return Shape.isOverlappingPolyRectangle(this, shape);
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
    public void draw(Renderer renderer) {
        renderer.fillPolygon(points, getX(), getY());
    }

    private void drawPoints(Renderer renderer, RColor color) {
        int radius = 2;
        renderer.setForegroundColor(color);
        for (int i = 0; i < numPoints; i++) {
            Vector2D point = points[i];
            renderer.fillCircle(point.getX(), point.getY(), radius);
        }
    }

    private void drawNormals(Renderer renderer, RColor color) {
        double scale = 100;
        renderer.setForegroundColor(color);
        double x = getX();
        double y = getY();
        for (Vector2D normal : normals) {
            renderer.drawLine(x, y, x + normal.getX() * scale, y + normal.getY() * scale);
        }
    }

    private void setupPoints(double[] xPoints, double[] yPoints) {
        for (int i = 0; i < numPoints; i++) {
            points[i] = new Vector2D(xPoints[i], yPoints[i]);
        }
    }

    private void setupMaxMin() {
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        for (Vector2D point : points) {
            double x = point.getX();
            double y = point.getY();
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        width = maxX - minX;
        height = maxY - minY;
    }

    private void setupNormalsAndShadows() {
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
