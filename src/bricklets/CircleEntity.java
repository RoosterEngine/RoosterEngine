package bricklets;

import gameengine.collisiondetection.shapes.CircleShape;

import java.awt.*;

public class CircleEntity extends TestingEntity {
    protected double radius;
    private double maxSpeed = 2;

    public CircleEntity(double x, double y, double radius) {
        super(x, y, new CircleShape(radius));
//        super(x, y, PolygonShape.getCircle(radius, 10));
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void update(double elapsedTime) {
//        enforceMaxSpeed();
    }

    private void enforceMaxSpeed() {
//        maxSpeed *= 1.001;
        double currentVel = Math.sqrt(dx * dx + dy * dy);
        double ratio = 0.99;
        if (Math.abs(dx) > Math.abs(dy)) {
            dx *= ratio;
        } else {
            dy *= ratio;
        }
        ratio = 0.99;
        dx *= ratio + (1 - ratio) * maxSpeed / currentVel;
        dy *= ratio + (1 - ratio) * maxSpeed / currentVel;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
