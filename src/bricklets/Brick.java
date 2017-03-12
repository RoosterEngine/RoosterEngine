package bricklets;

import gameengine.collisiondetection.shapes.Rectangle;
import gameengine.entities.Entity;
import gameengine.entities.defaults.rendering.FillShape;
import gameengine.graphics.Renderer;

/**
 * A brick to be used in brick breaker levels.
 *
 * User: davidrusu
 */
public class Brick extends Entity implements FillShape {
    private static final double TOTAL_HEALTH = 100;
    private double health = TOTAL_HEALTH;

    public Brick(double x, double y, double width, double height) {
        super(x, y, new Rectangle(width, height));
    }

    public void doDamage(double amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void setForegroundColor(Renderer renderer) {
        double offset = 0.1;
        float grad = (float) (((1 - health / TOTAL_HEALTH) + offset) / (offset + 1));
        renderer.setForegroundColor(grad, grad, grad);
    }
}
