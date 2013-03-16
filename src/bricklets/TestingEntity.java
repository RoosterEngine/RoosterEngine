package bricklets;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;

import java.awt.*;

/**
 * documentation
 * User: davidrusu
 * Date: 15/03/13
 * Time: 11:13 PM
 */
abstract class TestingEntity extends Entity {
    protected Color color = Color.WHITE;

    public TestingEntity(double x, double y, Shape shape) {
        super(x, y, shape);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
