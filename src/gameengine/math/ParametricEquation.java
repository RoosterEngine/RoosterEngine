/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.math;

/**
 *
 * @author davidrusu
 */
public abstract class ParametricEquation implements Function {
    private double delta = 1E-4, halfDelta = delta * 0.5;
    private double start, stop;

    public ParametricEquation(double start, double stop) {
        this.start = start;
        this.stop = stop;
    }

    public double getStart() {
        return start;
    }

    public double getStop() {
        return stop;
    }

    public void setRange(double start, double stop) {
        this.start = start;
        this.stop = stop;
    }

    public double slope(double t){
        return getDY(t) / getDX(t);
    }

    public double getDX(double t) {
        double x1 = getX(t - halfDelta);
        double x2 = getX(t + halfDelta);
        return (x2 - x1) / delta;
    }

    public double getDY(double t) {
        double y1 = getY(t - halfDelta);
        double y2 = getY(t + halfDelta);
        return (y2 - y1) / delta;
    }

    @Override
    public double valueAt(double t) {
        double dx = getDX(t);
        double dy = getDY(t);
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getArcLength(double from, double to, double eps){
        return Utilities.adaptiveSimpsonsRule(this, from, to, eps);
    }

    public abstract double getX(double t);

    public abstract double getY(double t);
}
