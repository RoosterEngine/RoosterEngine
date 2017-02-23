package bricklets;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.graphics.MutableColor;

/**
 * documentation
 *
 * @author davidrusu
 */
abstract class TestingEntity extends Entity {
    protected MutableColor color = MutableColor.createWhiteInstance();

    public TestingEntity(double x, double y, Shape shape) {
        super(x, y, shape);
    }

    public MutableColor getColor() {
        return color;
    }

    public void setColor(MutableColor color) {
        this.color = color;
    }
}
