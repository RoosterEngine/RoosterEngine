package gameengine.entities;

import gameengine.collisiondetection.shapes.CircleShape;
import gameengine.physics.Material;

import java.awt.*;

public class CircleEntity extends Entity {
    protected double radius;
    protected Color color = Color.WHITE;

    public CircleEntity(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);
        setShape(new CircleShape(this, x, y, radius,
                Material.createMaterial(0, 1), 1));
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
        double maxSpeed = 1;
        double currentVel = Math.sqrt(dx * dx + dy * dy);
//        if (currentVel > maxSpeed) {
//            double ratio = maxSpeed / currentVel;
//            dx *= ratio;
//            dy *= ratio;
//        }
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
}
