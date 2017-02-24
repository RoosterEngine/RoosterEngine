package bricklets;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.graphics.RColor;

/**
 * documentation
 *
 * @author davidrusu
 */
abstract class TestingEntity extends Entity {
    protected RColor color = RColor.WHITE;

    public TestingEntity(double x, double y, Shape shape) {
        super(x, y, shape);
    }

    public RColor getColor() {
        return color;
    }

    public void setColor(RColor color) {
        this.color = color;
    }
}
