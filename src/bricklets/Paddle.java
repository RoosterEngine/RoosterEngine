package bricklets;

import gameengine.collisiondetection.shapes.Polygon;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;

/**
 * The paddle that is used by players to bounce balls.
 *
 * User: davidrusu
 */
public class Paddle extends TestingEntity {

    public Paddle(double x, double y, double width, double height) {
        super(x, y, createPaddlePolygon(width, height));
//        super(x, y, new Circle(Math.max(width, height)));
//        super(x, y, new Rectangle(width, height));
//        super(x, y, Polygon.getCircle(Math.max(width, height), 4));
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

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.setForegroundColor(color);
        getShape().draw(renderer, getX(), getY());

//        g.setColor(RColor.ORANGE);
//        double halfHeight = getHalfHeight();
//        g.drawString("dx: " + dx, (int) (x), (int) (y + halfHeight + 10));
//        g.drawString("dy: " + dy, (int) (x), (int) (y + halfHeight + 25));
    }
}
