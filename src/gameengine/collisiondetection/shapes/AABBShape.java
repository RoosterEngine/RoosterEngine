package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;

import java.awt.*;

/**
 * @author david
 */
public class AABBShape extends Shape {
    private double width, height;

    public AABBShape(double width, double height) {
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
        shape.collideWithAABB(this, maxTime, result);
    }

    @Override
    public void collideWithCircle(CircleShape circleShape, double maxTime, Collision result) {
        Shape.collideCircleAABB(circleShape, this, maxTime, result);
    }

    @Override
    public void collideWithAABB(AABBShape aabbShape, double maxTime, Collision result) {
        Shape.collideAABBAABB(this, aabbShape, maxTime, result);
    }

    @Override
    public void collideWithPolygon(PolygonShape polygonShape, double maxTime, Collision result) {
        Shape.collideAABBPoly(this, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Shape shape) {
        return shape.isOverlappingAABB(this);
    }

    @Override
    public boolean isOverlappingPolygon(PolygonShape shape) {
        return Shape.isOverlappingPolyAABB(shape, this);
    }

    @Override
    public boolean isOverlappingCircle(CircleShape shape) {
        return Shape.isOverlappingCircleAABB(shape, this);
    }

    @Override
    public boolean isOverlappingAABB(AABBShape shape) {
        return Shape.isOverlappingAABBAABB(shape, this);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        double x = getX();
        double y = getY();
        g.drawRect((int) (x - halfWidth), (int) (y - halfHeight), (int) width, (int) height);
    }
}
