package bricklets;

import java.awt.*;

public class CircleEntity extends Entity {
    protected double radius;
    protected Color color = Color.WHITE;

    public CircleEntity(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2);
        this.radius = radius;
    }

    public CircleEntity(Entity entity, double radius) {
        super(entity.x, entity.y, radius, radius, entity.mass);
        this.radius = radius;
    }

    public CircleShape getShape(Material material) {
        return new CircleShape(x, y, radius, this, material);
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void update(double elapsedTime) {
    }

    private void enforceMaxSpeed() {
        double maxSpeed = 2;
        double currentVel = Math.sqrt(dx * dx + dy * dy);
        if (currentVel > maxSpeed) {
            double ratio = maxSpeed / currentVel;
            dx *= ratio;
            dy *= ratio;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
    }
}
