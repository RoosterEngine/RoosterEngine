package gameengine.math;

/**
 * A {@link ParametricEquation} that models a circle.
 *
 * @author davidrusu
 */
public class CircleEquation extends ParametricEquation {
    private double radius;

    public CircleEquation(double radius) {
        super(0, Math.PI / radius * 2);
        this.radius = radius;
    }

    @Override
    public double getDX(double t) {
        return -Math.sin(t / radius) * radius;
    }

    @Override
    public double getDY(double t) {
        return Math.cos(t / radius) * radius;
    }

    @Override
    public double getX(double t) {
        return Math.cos(t / radius) * radius;
    }

    @Override
    public double getY(double t) {
        return Math.sin(t / radius) * radius;
    }
}
