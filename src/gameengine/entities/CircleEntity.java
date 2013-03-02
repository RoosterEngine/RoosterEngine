package gameengine.entities;

import gameengine.collisiondetection.shapes.CircleShape;

import java.awt.*;

public class CircleEntity extends Entity {
    protected double radius;
    protected Color color = Color.WHITE;
    private double maxSpeed = 2;

    public CircleEntity(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, new CircleShape(x, y, radius));
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
