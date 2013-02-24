/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameengine.collisiondetection.shapes;

import gameengine.collisiondetection.Collision;
import gameengine.physics.Material;

import java.awt.*;

public class CircleShape extends Shape {
    private double radius;

    public CircleShape(double x, double y, double radius, double mass) {
        super(x, y, radius, radius, mass);
        this.radius = radius;
    }

    public CircleShape(double x, double y, double radius, double mass, Material material) {
        super(x, y, radius, radius, mass, material);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
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
    public int getShapeType() {
        return TYPE_CIRCLE;
    }

    @Override
    public void draw(Graphics2D g, Color color) {
        g.setColor(color);
        g.drawOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
    }
}
