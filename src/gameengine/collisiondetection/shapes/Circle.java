/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.entities.Entity;
import gameengine.graphics.Renderer;

public class Circle extends Shape {
    private final double radius;

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
    public void collideWithShape(Entity current, Entity other, double maxTime, Collision result) {
        other.getShape().collideWithCircle(other, current, this, maxTime, result);
    }

    @Override
    public void collideWithCircle(Entity current, Entity other, Circle circleShape, double
            maxTime, Collision result) {
        Shape.collideCircleCircle(current, this, other, circleShape, maxTime, result);
    }

    @Override
    public void collideWithRectangle(Entity current, Entity other, Rectangle aabbShape, double
            maxTime, Collision result) {
        Shape.collideCircleRectangle(current, this, other, aabbShape, maxTime, result);
    }

    @Override
    public void collideWithPolygon(Entity current, Entity other, Polygon polygonShape, double
            maxTime, Collision result) {
        Shape.collideCirclePoly(current, this, other, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Entity current, Entity other) {
        return other.getShape().isOverlappingCircle(other, current, this);
    }

    @Override
    public boolean isOverlappingPolygon(Entity current, Entity other, Polygon shape) {
        return Shape.isOverlappingPolyCircle(other, shape, current, this);
    }

    @Override
    public boolean isOverlappingCircle(Entity current, Entity other, Circle shape) {
        return Shape.isOverlappingCircleCircle(current, this, other, shape);
    }

    @Override
    public boolean isOverlappingRectangle(Entity current, Entity other, Rectangle shape) {
        return Shape.isOverlappingCircleRectangle(current, this, other, shape);
    }

    @Override
    public void draw(Renderer renderer, double x, double y) {
        renderer.drawCircle(x, y, radius);
    }
}
