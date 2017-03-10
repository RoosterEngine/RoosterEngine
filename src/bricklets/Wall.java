package bricklets;

import gameengine.collisiondetection.shapes.Shape;
import gameengine.entities.Entity;
import gameengine.graphics.RColor;
import gameengine.graphics.Renderer;
import gameengine.physics.Material;

public class Wall extends Entity {
    public Wall(double x, double y, Shape shape) {
        super(x, y, Double.POSITIVE_INFINITY, Material.getSteel(), shape);
    }

    @Override
    public void update(double elapsedTime) {

    }

    @Override
    public void draw(Renderer renderer) {
        renderer.setForegroundColor(RColor.WHITE);
        getShape().draw(renderer, getX(), getY());
    }
}
