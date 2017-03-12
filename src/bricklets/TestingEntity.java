package bricklets;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;
import gameengine.physics.Material;

/**
 * documentation
 *
 * @author davidrusu
 */
public class TestingEntity extends Entity {
    protected RColor color = RColor.WHITE;

    public TestingEntity(double x, double y, Shape shape) {
        super(x, y, shape);
    }

    public TestingEntity(double x, double y, Shape shape, double mass, Material material) {
        super(x, y, mass, material, shape);
    }

    public RColor getColor() {
        return color;
    }

    public void setColor(RColor color) {
        this.color = color;
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.setForegroundColor(color);
        getShape().fill(renderer, x, y);
    }
}
