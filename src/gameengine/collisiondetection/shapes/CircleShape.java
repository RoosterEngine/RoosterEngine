/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;

import java.awt.*;

public class CircleShape extends Shape {
    private double radius;

    public CircleShape(double radius) {
        super(radius, radius);
        this.radius = radius;
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
    public void collideWithCircle(CircleShape circleShape, double maxTime, Collision result) {
        Shape.collideCircleCircle(this, circleShape, maxTime, result);
    }

    @Override
    public void collideWithAABB(AABBShape aabbShape, double maxTime, Collision result) {
        Shape.collideCircleAABB(this, aabbShape, maxTime, result);
    }

    @Override
    public void collideWithPolygon(PolygonShape polygonShape, double maxTime, Collision result) {
        Shape.collideCirclePoly(this, polygonShape, maxTime, result);
    }

    @Override
    public boolean isOverlappingShape(Shape shape) {
        return shape.isOverlappingCircle(this);
    }

    @Override
    public boolean isOverlappingPolygon(PolygonShape shape) {
        return Shape.isOverlappingPolyCircle(shape, this);
    }

    @Override
    public boolean isOverlappingCircle(CircleShape shape) {
        return Shape.isOverlappingCircleCircle(this, shape);
    }

    @Override
    public boolean isOverlappingAABB(AABBShape shape) {
        return Shape.isOverlappingCircleAABB(this, shape);
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        double x = getX();
        double y = getY();
        g.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
    }
}
