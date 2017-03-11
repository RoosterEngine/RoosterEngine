package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.graphics.Renderer;

/**
 * @author david
 */
public class Rectangle extends Shape {
    private final double width, height;

    public Rectangle(double width, double height) {
        super(width * 0.5, height * 0.5);
        this.width = width;
        this.height = height;
    }

    @Override
    public double getArea() {
        return width * height;
    }

    @Override
    public void collideWithShape(Entity current, Entity other, double maxTime, Collision result) {
        other.getShape().collideWithRectangle(other, current, this, maxTime, result);
    }

    @Override
    public void collideWithCircle(Entity current, Entity other, Circle circleShape, double
            maxTime, Collision result) {
        Shape.collideCircleRectangle(other, circleShape, current, this, maxTime, result);
    }

    @Override
    public void collideWithRectangle(Entity current, Entity other, Rectangle aabbShape, double
            maxTime, Collision result) {
        Shape.collideRectangleRectangle(other, this, other, aabbShape, maxTime, result);
    }

    @Override
    public void collideWithPolygon(Entity current, Entity other, Polygon polygonShape, double
            maxTime, Collision result) {
        Shape.collideRectanglePoly(current, this, other, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Entity current, Entity other) {
        return other.getShape().isOverlappingRectangle(other, current, this);
    }

    @Override
    public boolean isOverlappingPolygon(Entity current, Entity other, Polygon shape) {
        return Shape.isOverlappingPolyRectangle(other, shape, current, this);
    }

    @Override
    public boolean isOverlappingCircle(Entity current, Entity other, Circle shape) {
        return Shape.isOverlappingCircleRectangle(other, shape, current, this);
    }

    @Override
    public boolean isOverlappingRectangle(Entity current, Entity other, Rectangle shape) {
        return Shape.isOverlappingRectangleRectangle(other, shape, current, this);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public void draw(Renderer renderer, double x, double y) {
        renderer.drawRect(x, y, getHalfWidth(), getHalfHeight());
    }

    @Override
    public void fill(Renderer renderer, double x, double y) {
        renderer.fillRect(x, y, getHalfWidth(), getHalfHeight());
    }
}
