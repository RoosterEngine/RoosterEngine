package gameengine.math;

import java.awt.*;

/**
 * A class for making Bezier curves
 *
 * User: davidrusu
 * Date: 10/12/12
 * Time: 5:43 PM
 */
public class BezierEquation extends ParametricEquation {
    private double x0, y0, x1, y1, x2, y2;

    public BezierEquation(double x0, double y0, double x1, double y1, double x2, double y2) {
        super(0, 1);
        setPoints(x0, y0, x1, y1, x2, y2);
    }

    private double evalutate(double p0, double p1, double p2, double t) {
        double timeLeft = 1 - t;
        double weight1 = timeLeft * timeLeft;
        double weight2 = 2 * t * timeLeft;
        return weight1 * p0 + weight2 * p1 + t * t * p2;
    }

    private double evaluateDerivative(double p0, double p1, double p2, double t) {
        return 2 * ((1 - t) * (p1 - p0) + t * (p2 - p1));
    }

    @Override
    public double getX(double t) {
        return evalutate(x0, x1, x2, t);
    }

    @Override
    public double getY(double t) {
        return evalutate(y0, y1, y2, t);
    }

    @Override
    public double getDX(double t) {
        return evaluateDerivative(x0, x1, x2, t);
    }

    @Override
    public double getDY(double t) {
        return evaluateDerivative(y0, y1, y2, t);
    }

    public final void setPoints(double x0, double y0, double x1, double y1, double x2, double y2) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        double lastX = getX(0);
        double lastY = getY(0);
        for (double t = 0; t <= 1; t += 0.01) {
            double x = getX(t);
            double y = getY(t);
            g.drawLine((int) lastX, (int) lastY, (int) x, (int) y);
            lastX = x;
            lastY = y;
        }
        g.setColor(Color.MAGENTA);
        double radius = 3;
        g.fillOval((int) (x0 - radius), (int) (y0 - radius), (int) (radius * 2), (int) (radius * 2));
        g.fillOval((int) (x1 - radius), (int) (y1 - radius), (int) (radius * 2), (int) (radius * 2));
        g.fillOval((int) (x2 - radius), (int) (y2 - radius), (int) (radius * 2), (int) (radius * 2));
    }
}