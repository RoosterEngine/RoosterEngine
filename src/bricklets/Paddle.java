package bricklets;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: davidrusu
 * Date: 10/12/12
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Paddle extends Entity {
    private Polygon polygon;
    private Color color;


    public Paddle(double x, double y, double width, double height, Material material) {
        super(x, y, width, height);
        color = Color.WHITE;
        createPaddlePolygon(x, y, width, height, material);
    }

    private void createPaddlePolygon(double x, double y, double width, double height, Material material) {
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
        double phaseLength =  Math.PI / (numPoints - 1);
        for (int i = 0; i < numPoints; i++) {
            double xPoint = Math.cos(phaseLength * i) * halfWidth;
            double yPoint = -Math.sin(phaseLength * i) * halfHeight;
            xPoints[i] = xPoint;
            yPoints[i] = yPoint;
        }

        polygon = new Polygon(x, y, xPoints, yPoints, this, material);
    }

    public Polygon getPolygon(){
        return polygon;
    }

    @Override
    public void update(double elapsedTime){
    }

    @Override
    public void draw(Graphics2D g) {
        polygon.draw(g, color);
    }
}
