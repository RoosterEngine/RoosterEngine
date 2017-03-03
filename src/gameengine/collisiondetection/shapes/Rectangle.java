package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.graphics.Renderer;

/**
 * @author david
 */
public class Rectangle extends Shape {
    private double width, height;

    public Rectangle(double width, double height) {
        super(width * 0.5, height * 0.5);
        init(width, height);
    }

    @Override
    public double getArea() {
        return width * height;
    }

    private void init(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void collideWithShape(Shape shape, double maxTime, Collision result) {
        shape.collideWithRectangle(this, maxTime, result);
    }

    @Override
    public void collideWithCircle(Circle circleShape, double maxTime, Collision result) {
        Shape.collideCircleRectangle(circleShape, this, maxTime, result);
    }

    @Override
    public void collideWithRectangle(Rectangle aabbShape, double maxTime, Collision result) {
        Shape.collideRectangleRectangle(this, aabbShape, maxTime, result);
    }

    @Override
    public void collideWithPolygon(Polygon polygonShape, double maxTime, Collision result) {
        Shape.collideRectanglePoly(this, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Shape shape) {
        return shape.isOverlappingRectangle(this);
    }

    @Override
    public boolean isOverlappingPolygon(Polygon shape) {
        return Shape.isOverlappingPolyRectangle(shape, this);
    }

    @Override
    public boolean isOverlappingCircle(Circle shape) {
        return Shape.isOverlappingCircleRectangle(shape, this);
    }

    @Override
    public boolean isOverlappingRectangle(Rectangle shape) {
        return Shape.isOverlappingRectangleRectangle(shape, this);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.drawRect(getX(), getY(), getHalfWidth(), getHalfHeight());
    }
}
