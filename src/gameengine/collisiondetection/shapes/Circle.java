/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.graphics.Renderer;

public class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        super(radius, radius);
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Circle(Radius: " + radius + ")";
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public void collideWithShape(Shape shape, double maxTime, Collision result) {
        shape.collideWithCircle(this, maxTime, result);
    }

    @Override
    public void collideWithCircle(Circle circleShape, double maxTime, Collision result) {
        Shape.collideCircleCircle(this, circleShape, maxTime, result);
    }

    @Override
    public void collideWithRectangle(Rectangle aabbShape, double maxTime, Collision result) {
        Shape.collideCircleRectangle(this, aabbShape, maxTime, result);
    }

    @Override
    public void collideWithPolygon(Polygon polygonShape, double maxTime, Collision result) {
        Shape.collideCirclePoly(this, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Shape shape) {
        return shape.isOverlappingCircle(this);
    }

    @Override
    public boolean isOverlappingPolygon(Polygon shape) {
        return Shape.isOverlappingPolyCircle(shape, this);
    }

    @Override
    public boolean isOverlappingCircle(Circle shape) {
        return Shape.isOverlappingCircleCircle(this, shape);
    }

    @Override
    public boolean isOverlappingRectangle(Rectangle shape) {
        return Shape.isOverlappingCircleRectangle(this, shape);
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.drawCircle(getX(), getY(), radius);
    }
}
