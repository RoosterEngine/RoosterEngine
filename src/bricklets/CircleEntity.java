package bricklets;

import gameengine.collisiondetection.shapes.Circle;
import gameengine.graphics.Renderer;

public class CircleEntity extends TestingEntity {
    protected double radius;
    private double maxSpeed = 2;

    public CircleEntity(double x, double y, double radius) {
        super(x, y, new Circle(radius));
//        super(x, y, Polygon.getCircle(radius, 10));
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.setForegroundColor(color);
        renderer.fillCircle(x, y, radius);
    }
}
