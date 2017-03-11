package bricklets;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.graphics.RColor;
import gameengine.physics.Material;

public class Wall extends TestingEntity {
    public Wall(double x, double y, Shape shape) {
        super(x, y, shape, Double.POSITIVE_INFINITY, Material.getRubber());
        setColor(RColor.WHITE);
    }
}
