package bricklets;

import gameengine.collisiondetection.shapes.Polygon;
import gameengine.graphics.RColor;

/**
 * The paddle that is used by players to bounce balls.
 *
 * User: davidrusu
 */
public class Paddle extends TestingEntity {
    public Paddle(double x, double y, double width, double height) {
        super(x, y, createPaddlePolygon(width, height));
        color = RColor.WHITE;
    }

    private static Polygon createPaddlePolygon(double width, double height) {
        double halfWidth = width * 0.5;
        double halfHeight = height * 0.5;

        int numPoints = 20;
        double topToBaseRatio = 0.8;
        double topHeight = height * topToBaseRatio;
        double baseHeight = height - topHeight;
        double[] xPoints = new double[numPoints + 2];
        double[] yPoints = new double[numPoints + 2];
        xPoints[numPoints] = -halfWidth;
        yPoints[numPoints] = baseHeight;
        xPoints[numPoints + 1] = halfWidth;
        yPoints[numPoints + 1] = baseHeight;
        double phaseLength = Math.PI / (numPoints - 1);
        for (int i = 0; i < numPoints; i++) {
            double xPoint = Math.cos(phaseLength * i) * halfWidth;
            double yPoint = -Math.sin(phaseLength * i) * halfHeight;
            xPoints[i] = xPoint;
            yPoints[i] = yPoint;
        }

        return new Polygon(xPoints, yPoints);
    }
}
