package gameengine.collisiondetection.shapes;

import gameengine.entities.Entity;
import gameengine.math.Utils;
import gameengine.physics.Material;

import java.awt.*;

/**
 * @author david
 */
public class AABBShape extends Shape {
    private double width, height, halfWidth, halfHeight;

    public AABBShape(Entity parent, double x, double y, double width,
                     double height, Material material, double mass) {
        super(parent, x, y, Utils.pythagoras(width / 2, height / 2),
                material, mass);
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getHalfWidth() {
        return halfWidth;
    }

    public double getHalfHeight() {
        return halfHeight;
    }

    @Override
    public int getShapeType() {
        return Shape.TYPE_AABB;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.drawRect((int) (x - halfWidth), (int) (y - halfHeight), (int) width, (int) height);
    }
}