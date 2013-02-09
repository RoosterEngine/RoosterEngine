package bricklets;

import gameengine.collisiondetection.shapes.PolygonShape;
import gameengine.entities.Entity;
import gameengine.physics.Material;

import java.awt.*;

/**
 * The paddle that is used by players to bounce balls
 * <p/>
 * User: davidrusu
 * Date: 10/12/12
 * Time: 6:37 PM
 */
public class Paddle extends Entity {
    private Color color;


    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
        color = Color.WHITE;
        createPaddlePolygon(x, y);
    }

    private void createPaddlePolygon(double x, double y) {
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

        setShape(new PolygonShape(this, x, y, xPoints, yPoints, Material.createMaterial(1, 1), 1));
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Graphics2D g) {
        getShape().draw(g, color);
        g.setColor(Color.ORANGE);
        g.drawString("dx: " + dx, (int) (x), (int) (y + halfHeight + 10));
        g.drawString("dy: " + dy, (int) (x), (int) (y + halfHeight + 25));
    }
}
